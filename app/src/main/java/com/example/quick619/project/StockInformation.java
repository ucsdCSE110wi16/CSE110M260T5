package com.example.quick619.project;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class StockInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_information);
    }

    public void confirmChanges(View v) {
        startActivity(new Intent(StockInformation.this, MainActivity.class));
    }

    public void nothing(){}
}
