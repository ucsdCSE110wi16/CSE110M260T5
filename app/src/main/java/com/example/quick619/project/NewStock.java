package com.example.quick619.project;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;

public class NewStock extends AppCompatActivity {

/*    private ArrayList<String> stockList = new ArrayList<String>();
    private ListView stockListView = (ListView) findViewById(R.id.stockList);
    private ArrayAdapter<String> stockListAdapter=new
            ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            stockList);
*/

    private static getquote my_quote = new getquote();  //Stock fetching class API
    String stock_name;                                  //Stores name of stock

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SearchView search_view = (SearchView) findViewById(R.id.searchView);    //Search view var
        final TextView text_price = (TextView) findViewById(R.id.curPrice);     //Price text var
        final TextView text_change = (TextView) findViewById(R.id.curChange);   //Change text var

        //Search view listener
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            //While the input is changing
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            //After the user submits text
            public boolean onQueryTextSubmit(String query) {

                double my_price = 0;    //store price
                double my_change = 0;   //store change

                //Store the stock name (in all caps)
                stock_name = query.toUpperCase();

                //Use quote API class to get info
                try {
                    my_price = my_quote.getprice(query);
                    my_change = my_quote.getchange(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Price will be < 0 if the stock doesn't exist
                if(my_price < 0) {
                    text_price.setText("Error");
                    text_change.setText("Error");
                }
                //Set the text fields for Price and Change
                else
                    text_price.setText(Double.toString(my_price));
                    text_change.setText(Double.toString(my_change));

                //Set color of change text
                if(my_change > 0)
                    text_change.setTextColor(Color.GREEN);
                else if(my_change == 0)
                    text_change.setTextColor(Color.GRAY);
                else
                    text_change.setTextColor(Color.RED);
                return false;
            }

        });


 //       stockListView.setAdapter(stockListAdapter);
    }

    public void makeNewStock(View v) {
        /*final EditText editStockName = (EditText) findViewById(R.id.stockName);
        String stockName = editStockName.getText().toString();*/

        //String stockName = "fake";

        final EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        String upperThresh = editUpperThresh.getText().toString();

        final EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        String lowerThresh = editLowerThresh.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name", stock_name);    //Edited by Ty, using stock name from search query
        intent.putExtra("upper", upperThresh);
        intent.putExtra("lower", lowerThresh);

        startActivity(intent);



///        View view = findViewById(R.id.stockList);
  //      Snackbar.make(view, "New stock tracker created", Snackbar.LENGTH_LONG)
  //              .setAction("Action", null).show();
    }
}
