package com.example.quick619.project;

import java.io.IOException;
import java.text.DecimalFormat;


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


    private String ticker;
    private double price;
    private double change;
    private double botThresh;
    private double topThresh;
    private int refreshRate = 5;
    private int index;
    private int currentCount = 0;
    private boolean passed;
    private double crossedThresh;
    private String companyName;
    private long prevTime;
    DecimalFormat numberFormat = new DecimalFormat("0.00");

    public ActiveStock(String ticker, double price, double change, double botThresh, double topThresh, int refreshRate, int index, String companyName) {

        this.ticker = ticker;
        this.price = price;
        this.change = change;
        this.botThresh = botThresh;
        this.topThresh = topThresh;
        this.refreshRate = refreshRate;
        this.index = index;
        this.companyName = companyName;
        prevTime = System.currentTimeMillis();

    }

    public String toString(){
        String retString;
        retString = "Index: " + index + " Name: " + ticker + " Price: " + price + " Refresh: " + refreshRate + " CompanyName: " + companyName;
        return retString;
    }

    public boolean PriceCheck() throws IOException {
        getquote quote = new getquote();
        double oldPrice = price;
        price = quote.getprice(ticker);
        price = Double.parseDouble(numberFormat.format(price));

        if (oldPrice != price) {
            change = quote.getchange(ticker);
            change = Double.parseDouble(numberFormat.format(change));
            //Send update
            return true;
        }
        return false;
    }

    public boolean ThresholdCheck(){
        if (price >= topThresh) {
            passed = true;
            crossedThresh = topThresh;
        } else if (price <= botThresh) {
            passed = true;
            crossedThresh = botThresh;
        }
        return passed;
    }

    public void resetCurrentCount(){ currentCount = 0;}
    public void tickCurrentCount(){

        long curTime = System.currentTimeMillis();
        int change = Math.round((curTime - prevTime) / 1000);
        prevTime = curTime;
        currentCount += change;
    }
    public int getCurrentCount() {return currentCount;}

    public String getCompanyName(){ return companyName;}

    public void setIndex(int index){ this.index = index;}
    public int getIndex(){ return index;}

    public void setTicker(String name) { this.ticker = name; }
    public String getTicker() { return ticker; }

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
