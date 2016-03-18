package com.example.quick619.project;

import android.app.Application;
import android.content.ClipData;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.EditText;
import android.widget.SearchView;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Sam on 3/17/2016
 *
 * This Espresso test is used for the following scenario test:
 *
 * Given the User has entered a stock name and a refresh rate on the new stock screen
 * When he/she clicks the confirm button on the Add Stock page
 * Then the Stock is added to the User’s list of stocks on the home page, the timer for the stock
 * is initialized and pinging the database for updated stock price based on the refresh rate.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateNewStockTest {

    private String stockName;

    @Rule
    public ActivityTestRule<NewStock> mActivityRule = new ActivityTestRule<>(
            NewStock.class);


    // Enters a stock name and a refresh rate on the new stock screen, then clicks the confirm
    // button on the Add Stock/NewStock page
    @Before
    public void createNewStock() {

        stockName = "AAPL";

        // Sets refresh rate to "5 minutes"
        onView(withId(R.id.notifyTime)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("5 minutes"))).perform(click());

        // Enters stock name
        // TODO I don't know what the id for the "magnifying glass" on the search bar is.
        // TODO we need put the proper id there to do .perform(click()), just contact Sam if confused on what to do


        // onView(withId(R.id.searchView)).perform(click());
        // onView(withId(R.id.searchView)).perform(typeText(stockName), pressKey(66));
    }


    // Creates the new stock and checks if it was created correctly
    @Test
    public void checkStockWasInitialized() {

        //The confirm button is clicked
        onView(withId(R.id.confirm)).perform(click());

        // Checks if the new stock is created and is in the stock list
        // Credit to http://stackoverflow.com/questions/21604351/check-if-a-listview-has-an-specific-a-number-of-items-and-scroll-to-last-one-wi
        onData(instanceOf(ClipData.Item.class))
                .inAdapterView(allOf(withId(R.id.stockList), isDisplayed()))
                .atPosition(0)
                .check(matches(isDisplayed()));


        // TODO: This is for Ty because idk how to check this
        // Check if the stock timer is initialized and is pinging the database
        // Code it in this method
        // Note: the selected refresh rate is "5 minutes"

    }
}
