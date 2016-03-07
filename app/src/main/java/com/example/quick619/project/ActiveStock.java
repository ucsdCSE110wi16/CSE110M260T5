package com.example.quick619.project;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;


/**
 * Created by Ty on 2/26/2016.
 *
 * Edited by Sam on 2/28/2016.
 * - Changed the class to implement Parcelable so that the data in MainActivity (the list of stocks)
 *   can be persistent. Also added more member variables and methods
 *
 * Note: These are what the values of refresh correspond to:
 *
 * if refresh ==
 * 0: No refresh rate (should never be 0 for an actual stock in the list)
 * 1: 5 minutes         2: 10 minutes          3: 30 minutes
 * 4: 1 hour            5: 2 hours             6: 1 day             7: 1 week
 */

public class ActiveStock implements Parcelable {

    private String ticker;
   // private StockController control = new StockController();
    private double price;
    private double change;
    private double botThresh;
    private double topThresh;
    private int refreshRate = 5;
    private StockController control;



    private String name = "";
    private String cityState = "";
    private String phone = "";

    public ActiveStock(Parcel in) {
        ticker = in.readString();
        price = in.readDouble();
        change = in.readDouble();
        botThresh = in.readDouble();
        topThresh = in.readDouble();
        refreshRate = in.readInt();
    }

    public ActiveStock() {
        super();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(ticker);
        out.writeDouble(price);
        out.writeDouble(change);
        out.writeDouble(botThresh);
        out.writeDouble(topThresh);
        out.writeInt(refreshRate);
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<ActiveStock> CREATOR = new Parcelable.Creator<ActiveStock>() {
        public ActiveStock createFromParcel(Parcel in) {
            return new ActiveStock(in);
        }

        public ActiveStock[] newArray(int size) {
            return new ActiveStock[size];
        }
    };

    public void threshholdCheck() throws IOException{

    }

    public void UpdateStock(){

    }

    public void setController(StockController control) { this.control = control; }
    public StockController getController () { return control; }

    public void setName(String name) { this.ticker = this.name = name; }
    public String getName() { return name; }

    public String getTicker() { return ticker; }

    public void setCityState(String cityState) { this.cityState = cityState; }
    public String getCityState() { return cityState; }

    public void setPhone(String phone) { this.phone = phone; }
    public String getPhone() { return phone; }

    public void setUpper(double upper) { this.topThresh = upper; }
    public double getUpper() { return topThresh; }

    public void setLower(double lower) { this.botThresh = lower; }
    public double getLower() { return botThresh; }

    public void setRefresh(int refresh) { this.refreshRate = refresh; }
    public int getRefresh() { return refreshRate; }

    public void setPrice(double price) { this.price = price; }
    public double getPrice() { return price; }

    public void setChange(double change) { this.change = change; }
    public double getChange() { return change; }

}
