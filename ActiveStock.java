package com.example.quick619.project;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveStock {

    private String ticker;
    private StockController control;
    private getquote quote = new getquote();
    private int refreshRate;
    private double botThresh;
    private double topThresh;
    private double crossedThresh;
    private double price;
    private boolean passed = false;
    private int index;
    private Timer timer;


    public ActiveStock(String ticker, StockController control, int refreshRate,
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
    }

    public void threshholdCheck() throws IOException {
        while (!passed)
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    double oldPrice = price;
                    try {
                        price = quote.getprice(ticker);
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
                        control.sendUpdate();
                }
            }, 0, refreshRate);
    }
}
