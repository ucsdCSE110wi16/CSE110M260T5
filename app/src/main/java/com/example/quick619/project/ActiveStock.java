package com.example.quick619.project;

import android.os.CountDownTimer;

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
    private StockController control;
    private getquote quote = new getquote();
    private int refreshRate;
    private double botThresh;
    private double topThresh;
    private double crossedThresh;
    private double price;
    private double change;
    private boolean passed = false;
    private int index;
    DecimalFormat numberFormat = new DecimalFormat("#.00");


    private String name = "";
    private String cityState = "";
    private String phone = "";

    public ActiveStock() {
        super();
    }

    /*public ActiveStock(String ticker, StockController control, int refreshRate,
                       double topThresh, double botThresh, int index) throws IOException {
        this.ticker = ticker;
        this.control = control;
        this.botThresh = botThresh;
        this.topThresh = topThresh;
        this.refreshRate = refreshRate;
        this.index = index;
        this.price = quote.getprice(ticker);
        timer = new Timer();
        threshholdCheck();
        control.threshholdPassed(index, crossedThresh);
    }*/


    public void threshholdCheck() throws IOException {
            new CountDownTimer(refreshRate * 60000, refreshRate) {
                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    double oldPrice = price;
                    try {
                        price = quote.getprice(ticker);
                        price = Double.parseDouble(numberFormat.format(price));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (price >= topThresh) {
                        passed = true;
                        crossedThresh = topThresh;
                    } else if (price <= botThresh) {
                        passed = true;
                        crossedThresh = botThresh;
                    }
                    if (oldPrice != price)
                    {
                        try {
                            change = quote.getchange(ticker);
                            change = Double.parseDouble(numberFormat.format(change));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        control.sendUpdate();
                    }
                    if(!passed)
                        start();
                }
            }.start();
    }

    public void setController(StockController control) { this.control = control; }
    public StockController getController () { return control; }

    public void setName(String name) { this.ticker = this.name = name; }
    public String getName() { return name; }

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
