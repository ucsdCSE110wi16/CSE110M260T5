package com.example.quick619.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by maurice on 3/6/16.
 */
public class EditStock extends AppCompatActivity {

    TextView text_price;      //Price text var
    TextView text_change;     //Change text var
    TextView text_name;       //Name text var


    String name;
    int refresh = 0;
    double my_price;
    double my_change;
    int toEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = getIntent().getStringExtra("name");
        my_price = getIntent().getDoubleExtra("price", -1);
        my_change = getIntent().getDoubleExtra("change", -1);
        toEdit = getIntent().getIntExtra("stockNum", -1);

        // Should never happen
        if (toEdit == -1) { throw new IllegalArgumentException("Error: Attempting to edit an " +
                "invalid stock."); }

        text_name = (TextView) findViewById(R.id.stock_name);
        text_price = (TextView) findViewById(R.id.price);
        text_change = (TextView) findViewById(R.id.change);

        // Sets the edit threshold EditText fields
        EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        editUpperThresh.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});
        EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        editLowerThresh.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8, 2)});

        String sign = "";
        //Set color of change text
        if (my_change > 0) {
            text_change.setTextColor(Color.GREEN);
            sign = "+";
        }
        else if (my_change == 0) {
            text_change.setTextColor(Color.GRAY);
            sign = "";
        }
        else {
            text_change.setTextColor(Color.RED);
            sign = "";
        }

        //Set the text fields
        text_name.setText(name);
        text_price.setText(Double.toString(my_price));
        text_change.setText(sign + Double.toString(my_change));

        // Creates the dropdown list for the refresh rate
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
    }

    /** Prompts the list of stocks to edit the current stock's information */
    public void confirmEdit(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        final EditText editUpperThresh = (EditText) findViewById(R.id.upperThresh);
        final EditText editLowerThresh = (EditText) findViewById(R.id.lowerThresh);
        String upperThresh = editUpperThresh.getText().toString();
        String lowerThresh = editLowerThresh.getText().toString();


        // Makes sure lower threshold < my_price
        if (!(upperThresh.equals("") || lowerThresh.equals("")) &&
                (Double.valueOf(upperThresh) <= Double.valueOf(lowerThresh))) {
            Toast.makeText(getApplicationContext(), "The top baseline must be greater than the bottom",
                    Toast.LENGTH_LONG).show();
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

        // Edits the existing stock
        else {

            intent.putExtra("upper", upperThresh);
            intent.putExtra("lower", lowerThresh);
            intent.putExtra("refresh", refresh);
            intent.putExtra("editIndex", toEdit);

            // Closes previous activity and current activity, then goes back to main screen
            setResult(2);
            startActivity(intent);
            finish();
        }
    }

    /** Deletes the stock that is currently being edited */
    public void deleteStock(View v) {
        final Intent intent = new Intent(this, MainActivity.class);

        // Creates an alert message
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm Delete");
        alert.setMessage("Are you sure that you want to delete this stock?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                intent.putExtra("editIndex", toEdit);
                intent.putExtra("delete", 1);       // Tells MainActivity to delete this stock

                // Closes the current activity and reopens MainActivity
                setResult(2);
                startActivity(intent);
                finish();
            }
        });
        alert.show();
    }
}