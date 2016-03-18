package com.example.quick619.project;

import android.content.ClipData;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;

/**
 * Created by Quick619 on 3/17/2016.
 *
 * Scenario:
 * Given the user is on the home screen
 * When he/she clicks on the stock card
 * Then the screen shifts to the stock information screen with the
 * name, current rate, and the user entered baselines of the stock.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScenarioThreeTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test

    public void checkStockTest () throws InterruptedException {

        // Open the first stock
        onData(anything())
                .inAdapterView(withId(R.id.stockList))
                .atPosition(0)
                .perform(click());

        // Check that each textView exists
        onView(withId(R.id.tickerName)).check(matches(isDisplayed()));
        onView(withId(R.id.companyName)).check(matches(isDisplayed()));
        onView(withId(R.id.price)).check(matches(isDisplayed()));
        onView(withId(R.id.change)).check(matches(isDisplayed()));
        onView(withId(R.id.upperThresh)).check(matches(isDisplayed()));
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
        onView(withId(R.id.lowerThresh)).check(matches(isDisplayed()));
        onView(withId(R.id.textView4)).check(matches(isDisplayed()));

        synchronized (this) {
            Thread.sleep((long) 2000);
        }
    }
}
