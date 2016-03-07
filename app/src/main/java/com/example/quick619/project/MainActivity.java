package com.example.quick619.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.IOException;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Necessary to access API database
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Accesses the persistent data and recreates the stock list
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        numStocks = preferences.getInt("numStocks", -1);

        if (numStocks == -1) {
            numStocks = 0;
        }

        // Retrieving data from saved session
        else {
            for (int i = 0; i < numStocks; i++) {
                String json = preferences.getString("stock" + i, "");
                ActiveStock sr = gson.fromJson(json, ActiveStock.class);
                stockList.add(sr);
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
                String name = stockList.get(position).getName();

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
        }
    }


    /** Helper method that creates a new stock
     *
     * @param preferences The SharedPreferences used to store data
     * @param gson Used to store objects into preferences
     */
    private void createNewStock(SharedPreferences preferences, Gson gson) {
        ActiveStock sr1 = new ActiveStock();

        // Individually sets all required data fields for the ActiveStock
        String text = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price").toString();
        String change = getIntent().getStringExtra("change").toString();
        double priceVal = getIntent().getDoubleExtra("priceVal", -1);
        double changeVal = getIntent().getDoubleExtra("changeVal", -1);

        if (!getIntent().getStringExtra("upper").equals("")) {
            sr1.setUpper(Double.valueOf(getIntent().getStringExtra("upper")));
        }

        if (!getIntent().getStringExtra("lower").equals("")) {
            sr1.setLower(Double.valueOf(getIntent().getStringExtra("lower")));
        }

        sr1.setName(text);
        sr1.setCityState("Price: $ " + price);
        sr1.setPhone("Change: $ " + change);
        sr1.setPrice(priceVal);
        sr1.setChange(changeVal);
        sr1.setRefresh(getIntent().getIntExtra("refresh", 0));
        try {
            sr1.threshholdCheck();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent myService = new Intent(this, NotificationService.class);

        myService.putExtra("ActiveStock", sr1);
        myService.putExtra("ticker", text);
        myService.putExtra("price", price);
        myService.putExtra("topThresh", getIntent().getStringExtra("upper"));
        myService.putExtra("botThresh", getIntent().getStringExtra("lower"));
        myService.putExtra("refresh", sr1.getRefresh());
        startService(myService);


        // Updates the SharedPreferences/persistent data right after stock creation
        SharedPreferences.Editor prefsEditor = preferences.edit();
        String json = gson.toJson(sr1);
        prefsEditor.putString("stock" + numStocks, json);
        numStocks++;
        prefsEditor.putInt("numStocks", numStocks);
        prefsEditor.apply();

        stockList.add(sr1);
    }


    /** Handles when the "edit stock" button (pencil) is clicked */
    public void editOnClick(View v) {
        Intent editStock = new Intent(MainActivity.this, EditStock.class);

        int stock_index = (Integer)v.getTag();
        ActiveStock toEdit = stockList.get(stock_index);

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
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("stock" + position);

        // Rearranges the current sharedPreferences data by "shifting" the key names
        for (int i = position+1; i < numStocks; i++) {
            String sr = preferences.getString("stock" + i, "");

            // Changes the key name so it is consistent with the shifting ArrayList
            editor.putString("stock" + (i-1), sr);
            editor.remove("stock" + i);
        }

        numStocks--;
        editor.putInt("numStocks", numStocks);

        editor.apply();
    }

    /* NOT NEEDED BECAUSE ITS STORED DIRECTLY AFTER CREATION INSTEAD
     *
     *
    // Credit to http://stackoverflow.com/questions/151777/saving-activity-state-on-android
    // Answer #4
    // Allows for persistent data
    @Override
    protected void onPause() {
        super.onPause();


        editor.apply();


        editor.putInt("numStocks", numStocks);

        // Stores all stock names, prices, changes, and thresholds
        for (int i = 0; i < numStocks; i++) {
            editor.putLong("price" + String.valueOf(i),
                    Double.doubleToRawLongBits(stockList.get(i).getPrice()));
            editor.putLong("change" + String.valueOf(i),
                    Double.doubleToRawLongBits(stockList.get(i).getChange()));
            editor.putLong("upper" + String.valueOf(i),
                    Double.doubleToRawLongBits(stockList.get(i).getUpper()));
            editor.putLong("lower" + String.valueOf(i),
                    Double.doubleToRawLongBits(stockList.get(i).getLower()));

            editor.putString("name" + String.valueOf(i), stockList.get(i).getName());
            editor.putInt("refresh" + String.valueOf(i), stockList.get(i).getRefresh());
        }

        // Commit to storage
        editor.apply();

    }
*/
}