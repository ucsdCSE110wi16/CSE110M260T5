package com.example.quick619.project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * MainActivity:
     *
     * Purpose: Allows user to see their list of stocks
     * Buttons: NewStock, EditStock, ViewStock
     */
    private GoogleApiClient client;

    ArrayList<ActiveStock> stockList = new ArrayList<>();   // Holds the list of stocks
    int numStocks = 0;                                      // Number of stocks in the ArrayList
    NotificationService mService;                           // NotificationService object
    boolean mBound = false;                                 // Boolean for if Service is bounded
    Intent myService;                                       // Intent for starting service
    Context c;                                              // Our context
    ListView stockLV;                                       // ListView for the stocks
    DecimalFormat numberFormat = new DecimalFormat("0.00"); // DecimalFormatter 0.00


    /**
     * OnStart()
     * @param savedInstanceState
     *
     * Gets the saved preferences of stocks
     * Decides what to do depending on which activity we came from
     * Sets up the ListView
     * Starts the service for background stock updating
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Necessary to access API database
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Accesses the persistent data and recreates the stock list
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        Gson gson = new Gson();
        numStocks = preferences.getInt("numStocks", -1);

        //Stop any previous service, then start the new service
        myService = new Intent(this, NotificationService.class);
        c = getApplicationContext();
        boolean boo = c.stopService(myService);
        System.out.println("Service Stopped = " + boo);
        c.startService(myService);
        c.bindService(myService, mConnection, Context.BIND_AUTO_CREATE);

        if (numStocks == -1) {
            numStocks = 0;
        }

        // Retrieving data from saved session
        else {
            for (int i = 0; i < numStocks; i++) {
                String json = preferences.getString("stock" + i, "");
                ActiveStock sr = gson.fromJson(json, ActiveStock.class);
                stockList.add(sr);
                System.out.println(sr);
            }
        }

        //Set up list view
        setContentView(R.layout.activity_main);
        stockLV = (ListView) findViewById(R.id.stockList);

        // Runs if the user came from either NewStock or EditStock
        if(getIntent().getExtras() !=null) {

            int editIndex = getIntent().getIntExtra("editIndex", -1);

            // Runs if the user came from EditStock (checks if the "editIndex" extra exists)
            if (editIndex != -1)
                editStock(editIndex);

            // Runs if the user came from NewStock
            else if (getIntent().getStringExtra("price") != null)
                createNewStock(preferences, gson);
        }

        //Initialize the listview
        stockLV.setAdapter(new MyCustomBaseAdapter(this, stockList));
        stockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /** Brings the user to the stock's information page
             * @param a the AdapterView that controls the list of stocks
             * @param v the View that is the list of stocks
             * @position the clicked item's position on the list
             * @id the id of the clicked item
             */
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                // Retrieving each individual piece of data that is relevant
                // to the stock information screen
                double upInt = stockList.get(position).getUpper();
                double lowInt = stockList.get(position).getLower();
                double price = stockList.get(position).getPrice();
                double change = stockList.get(position).getChange();
                int refresh = stockList.get(position).getRefresh();
                String name = stockList.get(position).getTicker();
                String companyName = stockList.get(position).getCompanyName();

                String upper = String.valueOf(upInt);
                String lower = String.valueOf(lowInt);

                // Passing on the data to the stock information screen
                Intent intent = new Intent(MainActivity.this, StockInformation.class);
                intent.putExtra("upper", upper);
                intent.putExtra("lower", lower);
                intent.putExtra("price", price);
                intent.putExtra("change", change);
                intent.putExtra("refresh", refresh);
                intent.putExtra("name", name);
                intent.putExtra("companyName", companyName);
                startActivity(intent);
            }
        });
    }

    /**
     * On stop, unbind from the service
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            c.unbindService(mConnection);
            mBound = false;
            System.out.println("ACTIVITY HAS STOPPED, UNBINDING FROM SERVICE");
            // FOR SOME REASON THIS ISN'T DISCONNECTING FROM THE SERVICE :/
        }
    }

    /** Helper method that handles when a stock is edited
     *
     * @param editIndex the index of the stock being edited
     */
    private void editStock(int editIndex) {
        ActiveStock toEdit = stockList.get(editIndex);

        // Runs if the user told the app to delete the stock
        if (getIntent().getIntExtra("delete", 0) == 1) {
            removeStock(editIndex);
        }

        // Runs if the user told the app to edit the stock
        else {

            // Sets the new fields (if edited). If a field is not edited, then it
            // is not changed.
            if (!getIntent().getStringExtra("upper").equals(""))
                toEdit.setUpper(Double.valueOf(getIntent().getStringExtra("upper")));

            if (!getIntent().getStringExtra("lower").equals(""))
                toEdit.setLower(Double.valueOf(getIntent().getStringExtra("lower")));

            int newRefresh = getIntent().getIntExtra("refresh", 0);
            if (newRefresh != 0)
                toEdit.setRefresh(newRefresh);

            //Save the data
            StoreData(toEdit);
        }
    }


    /** Helper method that creates a new stock
     *
     * @param preferences The SharedPreferences used to store data
     * @param gson Used to store objects into preferences
     */
    private void createNewStock(SharedPreferences preferences, Gson gson) {

        // Individually gets all required data fields for the ActiveStock
        String text = getIntent().getStringExtra("name");;
        double priceVal = getIntent().getDoubleExtra("priceVal", -1);
        double changeVal = getIntent().getDoubleExtra("changeVal", -1);
        double topThresh = 0;
        
        // Only gets the threshold values if they exist
        if (!getIntent().getStringExtra("upper").equals("")) {
            topThresh = Double.valueOf(getIntent().getStringExtra("upper"));
            topThresh = Double.parseDouble(numberFormat.format(topThresh));
        }
        double botThresh = 0;
        if (!getIntent().getStringExtra("lower").equals("")) {
            botThresh = Double.valueOf(getIntent().getStringExtra("lower"));
            botThresh = Double.parseDouble(numberFormat.format(botThresh));
        }
        int refresh = getIntent().getIntExtra("refresh", 0);
        int index = stockList.size();
        String companyName = getIntent().getStringExtra("companyName");

        //Then it constructs the stock with this info
        ActiveStock sr1 = new ActiveStock(text, priceVal, changeVal, botThresh, topThresh, refresh, index, companyName);

        //Add stock to stock list
        stockList.add(sr1);

        // Updates the SharedPreferences/persistent data right after stock creation
        StoreData(sr1);
    }

    /** Handles when the "edit stock" button (pencil) is clicked */
    public void editOnClick(View v) {
        Intent editStock = new Intent(MainActivity.this, EditStock.class);

        int stock_index = (Integer)v.getTag();
        ActiveStock toEdit = stockList.get(stock_index);
        String companyName = stockList.get(stock_index).getCompanyName();

        editStock.putExtra("companyName", companyName);
        editStock.putExtra("name", toEdit.getTicker());
        editStock.putExtra("price", toEdit.getPrice());
        editStock.putExtra("change", toEdit.getChange());
        editStock.putExtra("stockNum", stock_index);
        startActivityForResult(editStock, 2);
    }

    /** Handles when the "add new stock" button (the plus button) is clicked */
    public void buttonOnClick(View v) {
        Intent makeNewStock = new Intent(MainActivity.this, NewStock.class);
        startActivityForResult(makeNewStock, 2);
    }

    /** Handles when the "refresh button" has been clicked*/
    public void refreshOnClick(View v) {
        stockLV.setAdapter(new MyCustomBaseAdapter(this, stockList));
    }

    /** Allows the previous activity to be closed if the current "NewStock" or "EditStock"
     * activity is completed.
     * E.g. We start in MainActivity, then go to NewStock. If NewStock creates a new stock
     * (i.e. the activity is completed), then the current MainActivity is closed, and NewStock
     * creates a new MainActivity activity. If the user presses the back button, then this isn't used
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }


    /** Removes the stock from the app and updates the list
     *
     * @param position the stock's position in the stockList view (starting at 0)
     */
    private void removeStock(int position) {

        // Removes the stock from the ArrayList and the persistent data/sharedPreferences
        stockList.remove(position);
        if(mBound) mService.RemoveStock(position);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("stock" + position);

        // Rearranges the current sharedPreferences data by "shifting" the key names
        for (int i = position+1; i < numStocks; i++) {
            String sr = preferences.getString("stock" + i, "");

            // Changes the key name so it is consistent with the shifting ArrayList
            editor.putString("stock" + (i - 1), sr);
            editor.remove("stock" + i);
        }

        // Decrement numStocks once the stock is removed
        numStocks--;
        editor.putInt("numStocks", numStocks);

        editor.apply();

        for (int i = 0; i < stockList.size(); i++){
            ActiveStock temp = stockList.get(i);
            System.out.println("Size " + stockList.size() + " Index " + temp.getIndex() + " Ticker: " + temp.getTicker());
            temp.setIndex(i);
            StoreData(temp);
            System.out.println(temp.getIndex());
        }
    }

    /**
     * Helper method to save all new preferences
     * @param sr1
     */
    public void StoreData(ActiveStock sr1){

        //Updates the preferences by storing the given stock
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();

        System.out.println(sr1.getTicker() + "- At Index: " + sr1.getIndex());

        String json = gson.toJson(stockList.get(sr1.getIndex()));
        prefsEditor.putString("stock" + sr1.getIndex(), json);
        if(sr1.getIndex() == numStocks){
            numStocks++;
            prefsEditor.putInt("numStocks", numStocks);
        }
        prefsEditor.apply();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to NotificationService, cast the IBinder and get NotificationService instance
            NotificationService.NotificationBinder binder = (NotificationService.NotificationBinder) service;
            mService = binder.getService();
            mBound = true;
            System.out.println("BINDED WITH SERVICE");


            for(int i = 0; i < stockList.size(); i++){
                if(mBound)
                {
                    mService.AddStock(stockList.get(i));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            System.out.println("UNBINDED FROM SERVICE");
        }
    };
}
