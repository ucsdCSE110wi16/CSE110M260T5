package com.example.quick619.project.project;

import com.example.quick619.project.ActiveStock;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.lang.System.err;
import static java.lang.System.out;

/**
 * Created by Maurice on 3/11/2016.
 */
public class ActiveStockTest extends TestCase {

	/* For the purposes of testing stock, a dummy stock is created with the
	 * following attributes
	 * ticker: POOP
	 * price: 420
	 * change: 42
	 * botThresh: 4.20
	 * topThresh: 421
	 * refreshRate: 1
	 * index: 0
	 * companyName: Poop United
	 */

    //Sets up a list of all stocks to make loops in test writing, cuz writing is long
    private ArrayList<ActiveStock> stocks = new ArrayList<ActiveStock>();

    //A stock with something in it
    private ActiveStock testStock;
    //A stock with empty/zero values
    private ActiveStock emptyStock;
    //A null object
    private ActiveStock nullStock;

    //Set up the variables to be used
    @Before
    public void setUpVariables(){
        err.println("[TEST SET: DECLARING AND INITIALIZING ALL TEST STOCKS]");
        //Declare and initialize the test stocks

        //A stock with something in it
        testStock = new ActiveStock("POOP", 420, 42.0, 4.20, 421, 1, 0, "Poop United");
        //A stock with empty/zero values
        emptyStock = new ActiveStock("", 0, 0, 0, 0, 0, 0, "");
        //A null object
        nullStock = null;

        stocks.add(testStock);
        stocks.add(emptyStock);
        stocks.add(nullStock);
    }

    //Simply tests how a stock will be printed
    @Test
    public void testToString() throws Exception {
        err.println("[TEST SET: toString()]");

        for(ActiveStock s : stocks){
            err.println("[TEST: Printing " + s.getTicker() + "]");
            out.println(s);
        }
    }

    //Test if a change in a stock's price can be detected
    @Test //TODO
    public void testPriceCheck() throws Exception {
        //Testing when there is no change in a stock's price
        err.println("[TEST SET: PriceCheck(), no changes]");

        for(ActiveStock s : stocks){
            if(s.PriceCheck())
                err.println("No price change in " + s.getTicker() + ".");
            else
                err.println("Detected price change in " + s.getTicker() + ".");
        }

        //TODO: Change price of the stocks

        for(ActiveStock s : stocks){
            if(s.PriceCheck())
                err.println("No price change in " + s.getTicker() + ".");
            else
                err.println("Detected price change in " + s.getTicker() + ".");
        }

        //TODO: Set price back to original values

    }

    //Test if the app can detect when a stock's price passes a threshold value
    @Test //TODO
    public void testThresholdCheck() throws Exception {
        //Set 1: Prices unchanged from original values
        err.println("[TEST SET: topThreshold(), original prices]");

        for(ActiveStock s : stocks){
            if(s.ThresholdCheck())
                out.println("Price of " + s.getTicker() + " crossed one of its thresholds");
            else
                out.println("Price of " + s.getTicker() + "  stayed within its thresholds");
        }

        //TODO: Edit values of each stock so that it passes its topThreshold

        //Set 2: Prices pass topThreshold
        err.println("[TEST SET: topThreshold(), pass topThreshold]");

        for(ActiveStock s : stocks){
            if(s.ThresholdCheck())
                out.println("Price of " + s.getTicker() + " crossed one of its thresholds");
            else
                out.println("Price of " + s.getTicker() + "  stayed within its thresholds");
        }

        //TODO: Edit values of each stock so that it passes its botThreshold

        //Set 3: Prices pass botThreshold
        err.println("[TEST SET: topThreshold(), pass botThreshold]");

        for(ActiveStock s : stocks){
            if(s.ThresholdCheck())
                out.println("Price of " + s.getTicker() + " crossed one of its thresholds");
            else
                out.println("Price of " + s.getTicker() + "  stayed within its thresholds");
        }
    }

    //Current count looks like a timer. All stocks, when initialized, have that timer
    //set at 0, so since tickCurrentCount() hasn't been called yet, they should all be
    //0 at this point
    @Test
    public void testGetCurrentCount() throws Exception {
        err.println("[TEST SET: getCurrentCount()]");

        //Go through all stocks to get their currentCount value
        for(ActiveStock s : stocks){
            if(s.getCurrentCount() == 0)
                out.println(s.getTicker() + " has a currentCount of 0.");
            else{
                out.println(s.getTicker() + "has a non-zero currentCount.");
                fail();
            }
        }
    }

    @Test
    public void testTickCurrentCount() throws Exception {
        err.println("[TEST SET: tickCurrentCount()]");

        //Go through all stocks to get their currentCount value
        for(ActiveStock s : stocks){
            s.tickCurrentCount();

            if(s.getCurrentCount() != 0)
                out.println(s.getTicker() + " has a non-zero currentCount.");
            else{
                out.println(s.getTicker() + "has a currentCount of 0.");
                fail();
            }
        }
    }

    @Test
    public void testResetCurrentCount() throws Exception {
        err.println("[TEST SET: resetCurrentCount()]");

        for(ActiveStock s : stocks){
            s.tickCurrentCount();
            s.resetCurrentCount();

            if(s.getCurrentCount() == 0)
                out.println(s.getTicker() + " has a currentCount of 0.");
            else{
                out.println(s.getTicker() + "has a non-zero currentCount.");
                fail();
            }

        }
    }

    //Test getting the company name, not the ticker, of a stock
    @Test
    public void testGetCompanyName() throws Exception {
        err.println("[TEST SET: getCompanyName()]");

        for(ActiveStock s : stocks){
            out.println(s.getTicker() + " belongs to " + s.getCompanyName());
        }
    }

    //Tests setting the index of a stock by giving it a temporary index number,
    //chceking it, then setting it back to its original value
    @Test
    public void testSetIndex() throws Exception {
        err.println("[TEST SET: setIndex(int)]");

        //Go through all the stocks in the list
        for(ActiveStock s : stocks){
            //Get the index of the stock, assuming it works
            int index = s.getIndex();

            //Set it to an arbitrary number, 1
            if(index != 1){
                s.setIndex(1);          //Attempt to set index to 1

                if(s.getIndex() != 1)   //If it's not 1, the test has failed
                    fail();
                else
                    s.setIndex(index);

            }else{
                //If it's already 1, just set it to 0
                s.setIndex(0);

                if(s.getIndex() != 0)
                    fail();
                else
                    s.setIndex(index);
            }

            //Test to see if you can set the index of a stock to a negative number
            s.setIndex(-1);
            if(s.getIndex() < 0)
                fail();
        }

    }

    //Test getting the index of a stock
    @Test
    public void testGetIndex() throws Exception {
        err.println("[TEST SET: getIndex()]");

        //Get the index of every stock, just check that they are non-negative
        for(ActiveStock s : stocks){
            int index = s.getIndex();

            if(index < 0){
                err.println("[ERROR: index of stock" + s.getTicker() + "is negative]");
                fail();
            }
        }
    }

    //Test setting a stock's ticker name by setting it to a temporary name, then
    //setting it back
    @Test
    public void testSetTicker() throws Exception {
        err.println("[TEST SET: setTicker(String)");

        String tempTicker;
        String originalTicker;
        boolean match;

        //Compare the stock ticker name to various preset strings
        for(ActiveStock s : stocks){
            originalTicker = s.getTicker();

            tempTicker = "ZZZZ";
            s.setTicker(tempTicker);

            //A boolean to keep track if the original ticker name matched with the
            //temporary ticker replacement name
            match = originalTicker.equals(tempTicker);

            //The stock's current ticker should be the same as tempTicker, so it is
            //the equivalent statement that was put into "match" so they should be the same
            //Show an error otherwise
            if( (s.getTicker().equals(originalTicker)) != match)
                fail();
        }
    }

    //Because of the nature of strings, a simple null check will suffice
    @Test
    public void testGetTicker() throws Exception {
        err.println("[TEST SET: getTicker()]");

        for(ActiveStock s : stocks){
            if(s.getTicker() == null)
                fail();
        }
    }

    //Test setting the upper bound with an arbitrary number and setting it back
    @Test
    public void testSetUpper() throws Exception {
        err.println("[TEST SET: setUpper(double)]");

        double tempBound;
        double originalBound;

        for(ActiveStock s : stocks){
            //Set the lower bound to a temporary value
            if(s.getLower() == 1000){
                tempBound = 500;
                originalBound = s.getLower();

                s.setLower(tempBound);

                if(s.getLower() != tempBound)
                    fail();
                else
                    s.setLower(originalBound);
            }else{
                tempBound = 1000;
                originalBound = s.getLower();

                s.setLower(tempBound);

                if(s.getLower() != tempBound)
                    fail();
                else
                    s.setLower(originalBound);
            }
        }
    }

    //Checks that the upper bound is greater than 0
    @Test
    public void testGetUpper() throws Exception {
        err.println("[TEST SET: getUpper()]");

        for(ActiveStock s : stocks){
            if(s.getUpper() < 0)
                fail();
        }
    }

    //Test setting the lower bound with an arbitrary number and setting it back
    @Test
    public void testSetLower() throws Exception {
        err.println("[TEST SET: setLower(double)]");

        double tempBound;
        double originalBound;

        for(ActiveStock s : stocks){
            //Set the lower bound to a temporary value
            if(s.getLower() == 1){
                tempBound = 0.5;
                originalBound = s.getLower();

                s.setLower(tempBound);

                if(s.getLower() != tempBound)
                    fail();
                else
                    s.setLower(originalBound);
            }else{
                tempBound = 1;
                originalBound = s.getLower();

                s.setLower(tempBound);

                if(s.getLower() != tempBound)
                    fail();
                else
                    s.setLower(originalBound);
            }
        }
    }

    //Checks that the lower bound is greater than 0
    @Test
    public void testGetLower() throws Exception {
        err.println("[TEST SET: getLower()]");

        for(ActiveStock s : stocks){
            if(s.getLower() < 0)
                fail();
        }
    }

    //Test setting the refresh rate of a stock
    @Test
    public void testSetRefresh() throws Exception {
        err.println("[TEST SET: setRefresh(int)]");

        int tempRate;
        int originalRate;

        for(ActiveStock s : stocks){
            //Set the lower bound to a temporary value
            if(s.getRefresh() == 1){
                tempRate = 2;
                originalRate = s.getRefresh();

                s.setRefresh(tempRate);

                if(s.getRefresh() != tempRate)
                    fail();
                else
                    s.setRefresh(originalRate);
            }else{
                tempRate = 1;
                originalRate = s.getRefresh();

                s.setRefresh(tempRate);

                if(s.getRefresh() != tempRate)
                    fail();
                else
                    s.setRefresh(originalRate);
            }
        }
    }

    //Check for a non-negative refresh rate
    @Test
    public void testGetRefresh() throws Exception {
        err.println("[TEST SET: getRefresh()]");

        for(ActiveStock s : stocks){
            if(s.getRefresh() < 0)
                fail();
        }
    }

    //Test setting the price of a stock by changing it and setting it back
    @Test
    public void testSetPrice() throws Exception {
        err.println("[TEST SET: setPrice(double)]");

        double tempPrice;
        double originalPrice;

        for(ActiveStock s : stocks){
            //Set the lower bound to a temporary value
            if(s.getRefresh() == 10){
                tempPrice = 20;
                originalPrice = s.getPrice();

                s.setPrice(tempPrice);

                if(s.getPrice() != tempPrice)
                    fail();
                else
                    s.setPrice(originalPrice);
            }else{
                tempPrice = 10;
                originalPrice = s.getPrice();

                s.setPrice(tempPrice);

                if(s.getPrice() != tempPrice)
                    fail();
                else
                    s.setPrice(originalPrice);
            }
        }
    }

    //Test getting the price of a stock by looking for a negative number
    @Test
    public void testGetPrice() throws Exception {
        err.println("[TEST SET: getPrice()]");

        for(ActiveStock s : stocks){
            if(s.getPrice() < 0)
                fail();
        }
    }

    //Test setting the change in a stock by changing it and setting it back
    @Test
    public void testSetChange() throws Exception {
        err.println("[TEST SET: setChange(double)]");

        double tempChange;
        double originalChange;

        for(ActiveStock s : stocks){
            //Set the lower bound to a temporary value
            if(s.getRefresh() == 10){
                tempChange = 20;
                originalChange = s.getChange();

                s.setChange(tempChange);

                if(s.getChange() != tempChange)
                    fail();
                else
                    s.setChange(originalChange);
            }else{
                tempChange = 10;
                originalChange = s.getChange();

                s.setChange(tempChange);

                if(s.getChange() != tempChange)
                    fail();
                else
                    s.setChange(originalChange);
            }
        }
    }

    //Test getting the change of a stock by just looking for an exception
    @Test
    public void testGetChange() throws Exception {
        err.println("[TEST SET: getPrice()]");

        for(ActiveStock s : stocks){
            try{
                s.getPrice();
            }
            catch(Exception e){
                fail();
            }

        }
    }
}