package com.example.quick619.project;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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
    PopupWindow popup = new PopupWindow();

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

        else {
            for (int i = 0; i < numStocks; i++) {

                String json = preferences.getString("stock" + i, "");
                ActiveStock sr = gson.fromJson(json, ActiveStock.class);

                /* THIS IS THE LONG WAY TO DO IT, THE ABOVE WAY WORKS BETTER
                ActiveStock sr = new ActiveStock();
                sr.setName(preferences.getString("name" + String.valueOf(i), ""));
                double price = Double.longBitsToDouble(preferences.getLong("price" + String.valueOf(i), (long)0.00));
                double change = Double.longBitsToDouble(preferences.getLong("change" + String.valueOf(i), (long)0.00));

                sr.setPrice(price);
                sr.setChange(change);
                sr.setCityState("Price: $ " + price);
                sr.setPhone("Change: $ " + change);
                sr.setRefresh(preferences.getInt("refresh" + String.valueOf(i), 0));

                double up = Double.longBitsToDouble(preferences.getLong("upper" + String.valueOf(i), -1));
                double low = Double.longBitsToDouble(preferences.getLong("lower" + String.valueOf(i), -1));
                if (up != -1) sr.setUpper(up);
                if (low != -1) sr.setLower(low);
                */

                stockList.add(sr);
            }
        }

        setContentView(R.layout.activity_main);

        //Potential New View and Adapter
        final ListView stockLV = (ListView) findViewById(R.id.stockList);

        // Runs if the user inputted a new stock from the NewStock activity
        if(getIntent().getExtras() !=null) {
            if (getIntent().getStringExtra("price") != null) {
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

                // Updates the SharedPreferences/persistent data right after stock creation
                SharedPreferences.Editor prefsEditor = preferences.edit();
                String json = gson.toJson(sr1);
                prefsEditor.putString("stock" + numStocks, json);
                numStocks++;
                prefsEditor.putInt("numStocks", numStocks);
                prefsEditor.apply();

                stockList.add(sr1);
            }
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

                String upper = String.valueOf(upInt);
                String lower = String.valueOf(lowInt);

                Intent intent = new Intent(MainActivity.this, StockInformation.class);
                intent.putExtra("upper", upper);
                intent.putExtra("lower", lower);
                intent.putExtra("price", price);
                intent.putExtra("change", change);
                intent.putExtra("refresh", refresh);
                startActivity(intent);
            }
        });
    }

    public void editOnClick(View v) {
        View layout = getLayoutInflater().inflate(R.layout.edit_stock, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.showAtLocation(v, Gravity.CENTER, 0, 0);
    }

    public void confirmEdit(View v) {
        popup.dismiss();
    }

    public void deleteStock(View v) {
        popup.dismiss();
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

    //TODO implement these 3 methods below once stocks are editable

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
        numStocks--;
        editor.putInt("numStocks", numStocks);

        // Rearranges the current sharedPreferences data by "shifting" the key names
        for (int i = position+1; i < numStocks; i++) {
            String sr = preferences.getString("stock" + i, "");

            // Changes the key name so it is consistent with the shifting ArrayList
            editor.putString("stock" + (i-1), sr);
            editor.remove("stock" + i);
        }
    }

    /** Updates one of the stock's baselines depending on user interaction
     * SAM WILL UPDATE IT ONCE THE EDITSTOCK ACTIVITY IS COMPLETE
     *
     * @param position the stock's position in the stockList view (starting at 0)
     * @param threshold the stock's new threshold
     * @param up true if the upper threshold is being updated, false if bottom.
     */
    private void updateThreshold(int position, double threshold, boolean up) {
        ActiveStock sr = stockList.get(position);

        if (up) sr.setUpper(threshold);
        else    sr.setLower(threshold);
    }

    /** Updates the refresh rate depending on user interaction
     * SAM WILL UPDATE IT ONCE THE EDITSTOCK ACTIVITY IS COMPLETE
     * I WILL ALSO TY/SHRAVAN/WHOEVER IS DOING PUSH NOTIFICATIONS TO VERIFY IF ITS OK
     *
     * @param position the stock's position in the stockList view (starting at 0)
     * @param refresh rate
     */
    private void updateRefresh(int position, int refresh) {
        ActiveStock sr = stockList.get(position);
        sr.setRefresh(refresh);
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