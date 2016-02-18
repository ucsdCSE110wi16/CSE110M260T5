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

    //getquote class
    private static getquote my_quote = new getquote();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SearchView search_view = (SearchView) findViewById(R.id.searchView);
        final TextView text_view = (TextView) findViewById(R.id.curPrice);

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                double my_price = 0;
                double my_change = 0;
                try {
                    my_price = my_quote.getprice(query);
                    my_change = my_quote.getchange(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(my_price < 0)
                    text_view.setText("Error");
                else
                    text_view.setText(Double.toString(my_price));

                if(my_change > 0)
                    text_view.setTextColor(Color.GREEN);
                else if(my_change == 0)
                    text_view.setTextColor(Color.GRAY);
                else
                    text_view.setTextColor(Color.RED);
                return false;
            }

        });


 //       stockListView.setAdapter(stockListAdapter);
    }

    public void makeNewStock(View v) {
        /*final EditText editStockName = (EditText) findViewById(R.id.stockName);
        String stockName = editStockName.getText().toString();*/

        String stockName = "fake";

        final EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        String upperThresh = editUpperThresh.getText().toString();

        final EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        String lowerThresh = editLowerThresh.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("name", stockName);
        intent.putExtra("upper", upperThresh);
        intent.putExtra("lower", lowerThresh);

        startActivity(intent);



///        View view = findViewById(R.id.stockList);
  //      Snackbar.make(view, "New stock tracker created", Snackbar.LENGTH_LONG)
  //              .setAction("Action", null).show();
    }
}
