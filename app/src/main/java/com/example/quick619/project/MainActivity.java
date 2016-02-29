package com.example.quick619.project;

import android.content.Intent;
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
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Necessary to access API database
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        //Potential New View and Adpater
        ListView stockLV = (ListView) findViewById(R.id.stockList);
        ArrayList<ActiveStock> stockList = GenerateTestStocks();

        if(getIntent().getExtras() !=null){
            String text = getIntent().getStringExtra("name");
            String price = getIntent().getStringExtra("price");
            String change = getIntent().getStringExtra("change");
            String color_flag = getIntent().getStringExtra("color");

            ActiveStock sr1 = new ActiveStock();
            sr1.setName(text);
            sr1.setCityState("Price: $ " + price);
            sr1.setPhone("Change: $ " + change);

            stockList.add(sr1);
        }

        stockLV.setAdapter(new MyCustomBaseAdapter(this, stockList));
        stockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String upper = getIntent().getStringExtra("upper");
                String lower = getIntent().getStringExtra("lower");
                Intent intent = new Intent(MainActivity.this, StockInformation.class);
                intent.putExtra("upper", upper);
                intent.putExtra("lower", lower);
                startActivity(intent);
            }
        });

        //SHOULD REMOVE THIS BECAUSE I REPLACED IT WITH A CUSTOM ADAPTER//
        /*ArrayList<String> stockList = new ArrayList<String>();

        stockList.add("Stock 1");
        stockList.add("Stock 2");
        stockList.add("Stock 3");
        stockList.add("Stock 4");

        if(getIntent().getExtras() !=null){
            String text = getIntent().getStringExtra("name");
            stockList.add(text);
        }

        ArrayAdapter<String> stockListAdapter=new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                stockList);
        stockLV.setAdapter(stockListAdapter);
        stockLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String upper = getIntent().getStringExtra("upper");
                String lower = getIntent().getStringExtra("lower");
                Intent intent = new Intent(MainActivity.this, StockInformation.class);
                intent.putExtra("upper", upper);
                intent.putExtra("lower", lower);
                startActivity(intent);
            }
        });*/

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    //Input test Method
    private ArrayList<ActiveStock> GenerateTestStocks(){

        ArrayList<ActiveStock> results = new ArrayList<ActiveStock>();

        ActiveStock sr1 = new ActiveStock();
        sr1.setName("AAPL");
        sr1.setCityState("Price");
        sr1.setPhone("Change");
        results.add(sr1);

        sr1 = new ActiveStock();
        sr1.setName("INTS");
        sr1.setCityState("Price");
        sr1.setPhone("Change");
        results.add(sr1);

        sr1 = new ActiveStock();
        sr1.setName("AAP");
        sr1.setCityState("Price");
        sr1.setPhone("Change");
        results.add(sr1);

        sr1 = new ActiveStock();
        sr1.setName("YUM");
        sr1.setCityState("Price");
        sr1.setPhone("Change");
        results.add(sr1);

        return results;
    }
        //TESTING
    public void listOnClick(View v) {
        startActivity(new Intent(MainActivity.this, StockInformation.class));
    }

    public void buttonOnClick(View v) {

        startActivity(new Intent(MainActivity.this, NewStock.class));

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
}
