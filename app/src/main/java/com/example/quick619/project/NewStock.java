package com.example.quick619.project;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class NewStock extends AppCompatActivity {

/*    private ArrayList<String> stockList = new ArrayList<String>();
    private ListView stockListView = (ListView) findViewById(R.id.stockList);
    private ArrayAdapter<String> stockListAdapter=new
            ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            stockList);
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stock);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
