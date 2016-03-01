package com.example.quick619.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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

        // Accesses the persistent data and recreates the stock list
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        numStocks = preferences.getInt("numStocks", -1);

        if (numStocks == -1) {
            numStocks = 0;
        }

        else {
            for (int i = 0; i < numStocks; i++) {
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

                stockList.add(sr);
            }
        }

        setContentView(R.layout.activity_main);

        // Necessary to access API database
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Potential New View and Adpater
        final ListView stockLV = (ListView) findViewById(R.id.stockList);

        // DONT TEST STOCKS ANYMORE, I GOT IT TO WORK WOO
        // ArrayList<ActiveStock> stockList = GenerateTestStocks();

        if(getIntent().getExtras() !=null){
            ActiveStock sr1 = new ActiveStock();

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

            // More extras
            sr1.setPrice(priceVal);
            sr1.setChange(changeVal);
            sr1.setRefresh(getIntent().getIntExtra("refresh", 0));

            numStocks++;
            stockList.add(sr1);
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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    public void listOnClick(View v) {
        startActivity(new Intent(MainActivity.this, StockInformation.class));
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.quick619.project/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.quick619.project/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Credit to http://stackoverflow.com/questions/151777/saving-activity-state-on-android
    // Answer #4
    // Allows for persistent data
    @Override
    protected void onPause() {
        super.onPause();

        // Store values between instances here
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
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

}