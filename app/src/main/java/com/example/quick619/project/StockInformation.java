package com.example.quick619.project;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class StockInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_information);

        // Creates the back button at the top of the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView text_price = (TextView) findViewById(R.id.price);      //Price text var
        TextView text_change = (TextView) findViewById(R.id.change);     //Change text var
        double price = getIntent().getDoubleExtra("price", 0);
        double change = getIntent().getDoubleExtra("change", 0);

        // Sets all of the proper TextView fields for the stock's information
        TextView name = (TextView)findViewById(R.id.tickerName);
        name.setText(updateName());

        TextView companyName = (TextView)findViewById(R.id.companyName);
        companyName.setText(updateCompanyName());

        TextView upper= (TextView)findViewById(R.id.upperThresh);
        upper.setText(updateUpper());

        TextView lower= (TextView)findViewById(R.id.lowerThresh);
        lower.setText(updateLower());

        String sign = "";
        //Set color of change text
        if (change > 0) {
            text_change.setTextColor(Color.GREEN);
            sign = "+";
        }
        else if (change == 0) {
            text_change.setTextColor(Color.GRAY);
            sign = "";
        }
        else {
            text_change.setTextColor(Color.RED);
            sign = "";
        }

        //Set the text fields for Price and Change
        text_price.setText("$" + Double.toString(price));
        text_change.setText(sign + Double.toString(change));
    }

    // Sets the top baseline/upper threshold's TextView text
    private String updateUpper(){
        String upper = "$" + getIntent().getStringExtra("lower");
        return upper;
    }

    // Sets the bottom baseline's TextView text
    private String updateLower(){
        String lower = "$" + getIntent().getStringExtra("upper");
        return lower;
    }

    // Sets the ticker name's TextView text
    private String updateName(){
        String name = getIntent().getStringExtra("name");
        return name;
    }

    // Sets the stock's company name TextView text
    private String updateCompanyName(){
        String companyName = getIntent().getStringExtra("companyName");
        return companyName;
    }
}
