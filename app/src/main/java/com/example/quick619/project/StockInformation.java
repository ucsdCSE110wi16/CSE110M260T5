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

        TextView upper= (TextView)findViewById(R.id.upperThresh);
        upper.setText(updateUpper());

        TextView lower= (TextView)findViewById(R.id.lowerThresh);
        lower.setText(updateLower());

        //Set the text fields for Price and Change
        text_price.setText(Double.toString(price));
        text_change.setText(Double.toString(change));

        //Set color of change text
        if (change > 0)
            text_change.setTextColor(Color.GREEN);
        else if (change == 0)
            text_change.setTextColor(Color.GRAY);
        else
            text_change.setTextColor(Color.RED);
    }

    public String updateUpper(){
        String upper = getIntent().getStringExtra("lower");
        return upper;

    }

    public String updateLower(){
        String lower = getIntent().getStringExtra("upper");
        return lower;

    }
}
