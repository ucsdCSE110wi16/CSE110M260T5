package com.example.quick619.project;

import android.os.Parcel;
import android.os.Parcelable;

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
public class ActiveStock {
    private String name = "";
    private String cityState = "";
    private String phone = "";

    // These fields, along with their associated methods, were added by Sam
    private double upper = 0;
    private double lower = 0;
    private int refresh = 0;
    private double price = 0;
    private double change = 0;

    public ActiveStock() {
        super();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }

    public String getCityState() {
        return cityState;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setUpper(double upper) { this.upper = upper; }
    public double getUpper() { return upper; }

    public void setLower(double lower) { this.lower = lower; }
    public double getLower() { return lower; }

    public void setRefresh(int refresh) { this.refresh = refresh; }
    public int getRefresh() { return refresh; }

    public void setPrice(double price) { this.price = price; }
    public double getPrice() { return price; }

    public void setChange(double change) { this.change = change; }
    public double getChange() { return change; }
}
