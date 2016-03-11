package com.example.quick619.project;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ty on 2/26/2016.
 * Custom adapter for creating the list of stocks ont he main_activity page
 * ListView with 3 text fields: Name, Price, Change
 */
public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<ActiveStock> stockArrayList;

    private LayoutInflater mInflater;

    /**Constructor*/
    public MyCustomBaseAdapter(Context context, ArrayList<ActiveStock> list) {
        stockArrayList = list;
        mInflater = LayoutInflater.from(context);
    }

    /**Get arraylist size*/
    public int getCount() {
        return stockArrayList.size();
    }
    /**Get the item at the given position*/
    public Object getItem(int position) {
        return stockArrayList.get(position);
    }
    /**get the item Id of the item*/
    public long getItemId(int position) {
        return position;
    }
    /**Get the view for each item*/
    public View getView(int position, View convertView, ViewGroup parent) {
        /**Get a new ViewHolder and get the views within it*/
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_view, null);
            holder = new ViewHolder();

            ImageView editButton = (ImageView) convertView.findViewById(R.id.stockInfo);
            editButton.setTag(position);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.price = (TextView) convertView.findViewById(R.id.main_price);
            holder.change = (TextView) convertView.findViewById(R.id.main_change);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /**Set the views of view holder*/

        /**Set the color and sign of change depending on whether it's -, 0 or +*/
        String sign;

        /**Positive*/
        if(stockArrayList.get(position).getChange() > 0){
            holder.change.setTextColor(Color.GREEN);
            sign = "+";
        }
        /**Negative*/
        else if(stockArrayList.get(position).getChange() < 0) {
            holder.change.setTextColor(Color.RED);
            sign = "";
        }
        /**Zero*/
        else {
            holder.change.setTextColor(Color.GRAY);
            sign = "";
        }

        /**Finally, set the text of each view*/
        holder.name.setText(stockArrayList.get(position).getTicker());
        holder.price.setText("$" + Double.toString(stockArrayList.get(position).getPrice()));
        holder.change.setText(sign + Double.toString(stockArrayList.get(position).getChange()));

        return convertView;
    }

    /**View holder class, holds the TextViews in the customer view*/
    static class ViewHolder {
        TextView name;
        TextView price;
        TextView change;
    }
}