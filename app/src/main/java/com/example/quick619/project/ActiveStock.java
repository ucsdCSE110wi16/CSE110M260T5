package com.example.quick619.project;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Ty on 2/26/2016.
 *
 * ActiveStock.class
 * Object class that holds all the data for one specific stock chosen by the user
 * Stock is in charge of holding all data corresponding to it such as price, name, change, etc.
 *
 * Edited by Sam on 2/28/2016.
 *
 */

public class ActiveStock {


    private String ticker;          //Ticker symbol
    private double price;           //Price
    private double change;          //Change in price since last database update
    private double botThresh;       //Bottom Threshold
    private double topThresh;       //Top Threshold
    private int refreshRate = 5;    //Refresh Rate, in minutes (default is 5 minutes)
    private int index;              //Index in the ArrayList
    private int currentCount = 0;   //Current count for clock, in seconds
    private boolean passed;         //Boolean for if a threshold has been passed
    private double crossedThresh;   //The double of the threshhold that was crossed
    private String companyName;     //Company Name
    private long prevTime;          //Last recorded time of last tick
    DecimalFormat numberFormat = new DecimalFormat("0.00");     //Decimal formatter 0.00

    /**
     *
     * @param ticker        Ticker name
     * @param price         Price of stock
     * @param change        Last change in price
     * @param botThresh     Bottom Threshold set by user
     * @param topThresh     Top Treshold set by user
     * @param refreshRate   Refresh rate for how often stock updates
     * @param index         Index in the ArrayList
     * @param companyName   Company Name
     */
    public ActiveStock(String ticker, double price, double change, double botThresh,
                       double topThresh, int refreshRate, int index, String companyName) {

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

    /**
     * prints the Name, Price, Refresh Rate, and Company name for debugging
     * @return String to be printed
     */
    public String toString(){
        String retString;
        retString = "Index: " + index + " Name: " + ticker + " Price: " + price + " Refresh: " + refreshRate + " CompanyName: " + companyName;
        return retString;
    }

    /**
     * Downloads new price of the current stock, and checks if it has changed
     * @return If price has changed, return true, else return false
     * @throws IOException
     */
    public boolean PriceCheck() throws IOException {
        getquote quote = new getquote();
        double oldPrice = price;
        price = quote.getprice(ticker) + 100;
        price = Double.parseDouble(numberFormat.format(price));

        if (oldPrice != price) {
            change = quote.getchange(ticker);
            change = Double.parseDouble(numberFormat.format(change));
            //Send update
            return true;
        }
        return false;
    }

    /**
     * Always called after PriceCheck() has been called
     * Checks if the new price is above the Top Threshold, or below the Bottom Threshold
     * If the threshold is 0 for either top or bottom, then it won't check against that Threshold
     * If the user doesn't set thresholds, it will be set as 0
     * @return returns true if either threshold has been passed, else returns false
     */
    public boolean ThresholdCheck(){
        if (price >= topThresh && topThresh != 0) {
            passed = true;
            crossedThresh = topThresh;
        } else if (price <= botThresh && topThresh != 0) {
            passed = true;
            crossedThresh = botThresh;
        }

        if(passed)
        {
            topThresh = botThresh = 0;
        }
        return passed;
    }

    /**resets the current clock count to 0*/
    public void resetCurrentCount(){ currentCount = 0;}


    /**Will update the time that has passed since last tick*/
    public void tickCurrentCount(){

        long curTime = System.currentTimeMillis();
        int change = Math.round((curTime - prevTime) / 1000);
        prevTime = curTime;
        currentCount += change;
    }

    /**Getter Methods*/
    public double getPrice() { return price; }
    public double getChange() { return change; }
    public int getRefresh() { return refreshRate; }
    public double getLower() { return botThresh; }
    public double getUpper() { return topThresh; }
    public int getCurrentCount() {return currentCount;}
    public String getCompanyName(){ return companyName;}
    public int getIndex(){ return index;}
    public String getTicker() { return ticker; }

    /**Setter Methods*/
    public void setIndex(int index){ this.index = index;}
    public void setUpper(double upper) { this.topThresh = upper; }
    public void setLower(double lower) { this.botThresh = lower; }
    public void setRefresh(int refresh) { this.refreshRate = refresh; }



}
