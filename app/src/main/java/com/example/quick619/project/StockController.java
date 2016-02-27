package com.example.quick619.project;

import java.util.ArrayList;

/**
 * Created by Ty on 2/26/2016.
 */

public class StockController {

    ArrayList<ActiveStock> stock_models;

    public void StockController(ArrayList<ActiveStock> stocks) {

        stock_models = stocks;
    }

    public void  addNewStock(ActiveStock stock){
        stock_models.add(stock);
    }

    public void removeStock(int position){
        stock_models.remove(position);
    }

    public void sendUpdates(){
        //Update adpater
    }

    public void threshholdPassed(int index, double threshhold){

    }
}
