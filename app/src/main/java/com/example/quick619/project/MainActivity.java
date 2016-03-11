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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *
     */
    private GoogleApiClient client;

    ArrayList<ActiveStock> stockList = new ArrayList<>();   // Holds the list of stocks
    int numStocks = 0;
    NotificationService mService;
    boolean mBound = false;
    Intent myService;
    Context c;


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

        setContentView(R.layout.activity_main);
        final ListView stockLV = (ListView) findViewById(R.id.stockList);

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

        stockLV.setAdapter(new MyCustomBaseAdapter(this, stockList));
        stockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                double upInt = stockList.get(position).getUpper();
                double lowInt = stockList.get(position).getLower();
                double price = stockList.get(position).getPrice();
                double change = stockList.get(position).getChange();
                int refresh = stockList.get(position).getRefresh();
                String name = stockList.get(position).getTicker();

                String upper = String.valueOf(upInt);
                String lower = String.valueOf(lowInt);

                Intent intent = new Intent(MainActivity.this, StockInformation.class);
                intent.putExtra("upper", upper);
                intent.putExtra("lower", lower);
                intent.putExtra("price", price);
                intent.putExtra("change", change);
                intent.putExtra("refresh", refresh);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

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

            //Sets the new fields (if edited)
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
        if (!getIntent().getStringExtra("upper").equals("")) {
            topThresh = Double.valueOf(getIntent().getStringExtra("upper"));
        }
        double botThresh = 0;
        if (!getIntent().getStringExtra("lower").equals("")) {
            botThresh = Double.valueOf(getIntent().getStringExtra("lower"));
        }
        int refresh = getIntent().getIntExtra("refresh", 0);
        int index = stockList.size();

        //Then it constructs the stock with this info
        ActiveStock sr1 = new ActiveStock(getApplicationContext(), text, priceVal, changeVal, botThresh, topThresh, refresh, index);

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

        editStock.putExtra("name", toEdit.getTicker());
        editStock.putExtra("price", toEdit.getPrice());
        editStock.putExtra("change", toEdit.getChange());
        editStock.putExtra("stockNum", stock_index);
        startActivityForResult(editStock, 2);
    }


    public void buttonOnClick(View v) {

        Intent makeNewStock = new Intent(MainActivity.this, NewStock.class);
        startActivityForResult(makeNewStock, 2);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==2){
            finish();
        }
    }


    /** Removes the stock from the app and updates the list
     * SAM WILL UPDATE IT ONCE THE EDITSTOCK ACTIVITY IS COMPLETE
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