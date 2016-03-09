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
 */
public class MyCustomBaseAdapter extends BaseAdapter {
    private static ArrayList<ActiveStock> searchArrayList;

    private LayoutInflater mInflater;

    public MyCustomBaseAdapter(Context context, ArrayList<ActiveStock> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_row_view, null);
            holder = new ViewHolder();

            ImageView editButton = (ImageView) convertView.findViewById(R.id.stockInfo);
            editButton.setTag(position);

            holder.txtName = (TextView) convertView.findViewById(R.id.name);
            holder.txtCityState = (TextView) convertView.findViewById(R.id.cityState);
            holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String sign = "";

        if(searchArrayList.get(position).getChange() > 0){
            holder.txtPhone.setTextColor(Color.GREEN);
            sign = "+";
        }
        else if(searchArrayList.get(position).getChange() < 0) {
            holder.txtPhone.setTextColor(Color.RED);
            sign = "-";
        }

        else {
            holder.txtPhone.setTextColor(Color.GRAY);
            sign = "";
        }

        holder.txtName.setText(searchArrayList.get(position).getTicker());
        holder.txtCityState.setText("Price: $" + Double.toString(searchArrayList.get(position).getPrice()));
        holder.txtPhone.setText("Change: " + sign + "$" + Double.toString(Math.abs(searchArrayList.get(position).getChange())));

        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        TextView txtCityState;
        TextView txtPhone;
        ImageView editButton;
    }
}