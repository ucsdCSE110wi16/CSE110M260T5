package com.example.quick619.project;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class NewStock extends AppCompatActivity {

/*    private ArrayList<String> stockList = new ArrayList<String>();
    private ListView stockListView = (ListView) findViewById(R.id.stockList);
    private ArrayAdapter<String> stockListAdapter=new
            ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            stockList);
*/

    SearchView search_view;   //Search view var
    TextView text_price;      //Price text var
    TextView text_change;     //Change text var
    ListView search_results;  //Search results var

    String StockList = "/src/main/assets/112"; //Need to fix this
    private static getquote my_quote = new getquote();  //Stock fetching class API
    String stock_name = "";                             //Stores name of stock
    String[] items;
    ArrayList<String> listItems;
    ArrayAdapter<String> adapter;
    double my_price = 0;    //store price
    double my_change = 0;   //store change
    int refresh = 0;
    int index = 0;
    DecimalFormat numberFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        search_view = (SearchView) findViewById(R.id.searchView);
        text_price = (TextView) findViewById(R.id.curPrice);
        text_change = (TextView) findViewById(R.id.curChange);
        search_results = (ListView) findViewById(R.id.searchResults);
        EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        editUpperThresh.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        editLowerThresh.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        // Retrieves the refresh rate value from the drop down list
        Spinner refreshList = (Spinner) findViewById(R.id.notifyTime);
        refreshList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                switch (pos){
                    case 1:
                        refresh = 5;
                        break;
                    case 2:
                        refresh = 10;
                        break;
                    case 3:
                        refresh = 30;
                        break;
                    case 4:
                        refresh = 60;
                        break;
                    case 5:
                        refresh = 60 * 2;
                        break;
                    case 6:
                        refresh = 24 * 60;
                        break;
                    case 7:
                        refresh = 24 * 60 * 7;
                        break;
                }

            }
            @Override
            public void onNothingSelected (AdapterView < ? > parent){}
        });

        try {
            initList(StockList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Listener for when user clicks on search bar
        search_view.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search_results.setVisibility(View.VISIBLE);
            }
        });

        //Listener for when user closes search bar
        search_view.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                search_results.setVisibility(View.INVISIBLE);
                return false;
            }
        });


        //Search view listener
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            //While the input is changing
            public boolean onQueryTextChange(String s) {
                search_results.setVisibility(View.VISIBLE);
                if (s.toString().equals("")) {
                    try {
                        initList(StockList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        initList(StockList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    searchItem(s.toString());
                }

                return false;
            }

            @Override
            //After the user submits text
            public boolean onQueryTextSubmit(String query) {

                search_results.setVisibility(View.INVISIBLE);

                //Store the stock name (in all caps)
                stock_name = query.toUpperCase();

                //Use quote API class to get info
                try {
                    my_price = my_quote.getprice(query);
                    my_price = Double.parseDouble(numberFormat.format(my_price));
                    my_change = my_quote.getchange(query);
                    my_change = Double.parseDouble(numberFormat.format(my_change));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Price will be < 0 if the stock doesn't exist
                if (my_price < 0) {
                    text_price.setText("Symbol Not Found");
                    text_change.setText("Please Try a Different Symbol");
                    my_change = 0;
                }
                //Set the text fields for Price and Change
                else {
                    text_price.setText(Double.toString(my_price));
                    text_change.setText(Double.toString(my_change));
                }

                //Set color of change text
                if (my_change > 0)
                    text_change.setTextColor(Color.parseColor("#FF1DB318"));
                else if (my_change == 0)
                    text_change.setTextColor(Color.GRAY);
                else
                    text_change.setTextColor(Color.RED);
                return false;
            }
        });

        //Listener for when the user clicks on a search result
        search_results.setOnItemClickListener(new ListView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedFromList = String.valueOf(search_results.getItemAtPosition(position));
                selectedFromList = selectedFromList.substring(0, selectedFromList.indexOf(' '));
                search_view.setQuery(selectedFromList, false);
            }
        });

    }

    //Initialize the Search Results List
    public void initList (String pathname) throws IOException {

        AssetManager am = getAssets();
        InputStream file = am.open("NASDAQ.txt");

        Scanner scanner = new Scanner(file);
        int index = 0;
        items = new String[3114];

        try {
            while(scanner.hasNextLine()) {
                items[index] = scanner.nextLine();
                index++;
            }
        } finally {
            scanner.close();
        }

        listItems = new ArrayList<>(Arrays.asList(items));
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.txtitem, listItems);
        search_results.setAdapter(adapter);

    }

    //Update the Search Results list
    public void searchItem (String txtToSearch) {
        for (String item:items) {
            if (!item.toLowerCase().contains(txtToSearch.toString().toLowerCase())) {
                listItems.remove(item);
            }

        }
        adapter.notifyDataSetChanged();
    }

    public void makeNewStock(View v) {

        final EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        String upperThresh = editUpperThresh.getText().toString();

        final EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        String lowerThresh = editLowerThresh.getText().toString();

        // Makes sure a valid stock/tracker has been entered
        if ((my_price < 0 )|| stock_name.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter a valid stock",
                    Toast.LENGTH_SHORT).show();
        }

        // Makes sure lower threshold < my_price
        else if (!(upperThresh.equals("") || lowerThresh.equals("")) &&
                (Double.valueOf(upperThresh) < Double.valueOf(lowerThresh))) {
            Toast.makeText(getApplicationContext(), "The top baseline must be greater than the bottom",
                    Toast.LENGTH_LONG).show();
        }

        // Makes sure a refresh rate was selected
        else if (refresh == 0) {
            Toast.makeText(getApplicationContext(), "Please select a refresh rate",
                    Toast.LENGTH_SHORT).show();
        }

        // Makes sure upper threshold > price
        else if (!(upperThresh.equals("")) &&
                (Double.valueOf(upperThresh) <= my_price)) {
            Toast.makeText(getApplicationContext(), "The top baseline must be greater than the price",
                    Toast.LENGTH_LONG).show();
        }

        // Makes sure lower threshold < price
        else if (!(lowerThresh.equals("")) &&
                (Double.valueOf(lowerThresh) >= my_price)) {
            Toast.makeText(getApplicationContext(), "The bottom baseline must be less than the price",
                    Toast.LENGTH_LONG).show();
        }


        else {

            //Make the view for the stock
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name", stock_name);    //Edited by Ty, using stock name from search query
            intent.putExtra("upper", upperThresh);
            intent.putExtra("lower", lowerThresh);
            intent.putExtra("refresh", refresh);
            System.out.println(refresh);


            //New intent info
            intent.putExtra("price", Double.toString(my_price));
            intent.putExtra("change", Double.toString(my_change));

            // More new intent info
            intent.putExtra("priceVal", my_price);
            intent.putExtra("changeVal", my_change);

            // Closes the old "MainActivity" once the new stock is confirmed to be created
            setResult(2);

            startActivity(intent);
            finish();
        }
    }
}