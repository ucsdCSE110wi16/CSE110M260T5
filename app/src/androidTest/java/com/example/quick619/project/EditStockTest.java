package com.example.quick619.project;

import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static org.hamcrest.Matchers.is;

/**
 * Created by Sam on 3/17/2016.
 *
 * Given the user is on the edit stock screen
 * When he/she enters either a new top baseline, bottom baseline and/or refresh rate
 * and clicks the confirm button
 * Then the screen shifts to the home screen and updates the newly entered baseline
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditStockTest {

    private double newTop, newBot;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);

    // Sets the new thresholds and start edit stock activity
    @Before
    public void initEnvironment() {
        newTop = 420.69;
        newBot = 0.01;

        onView(withId(R.id.stockInfo)).perform(click());
    }

    @Test
    public void editStockTest () throws InterruptedException {

        // Changes the threshold values
        onView(withId(R.id.upperThresh)).perform(typeText(String.valueOf(newTop)),
                ViewActions.closeSoftKeyboard());
        onView(withId(R.id.lowerThresh)).perform(typeText(String.valueOf(newBot)),
                ViewActions.closeSoftKeyboard());

        // Changes the refresh rate to "5 minutes"
        onView(withId(R.id.notifyTime)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("5 minutes"))).perform(click());
        onView(withId(R.id.notifyTime))
                .check(matches(withSpinnerText(containsString("5 minutes"))));

        // Confirm the edits
        onView(withId(R.id.confirmEdit)).perform(click());

        // Open the first/edited stock
        onData(anything())
                .inAdapterView(withId(R.id.stockList))
                .atPosition(0)
                .perform(click());

        // Check that the values are correct
        onView(withId(R.id.upperThresh)).check(matches(isDisplayed()));
        onView(withId(R.id.lowerThresh)).check(matches(isDisplayed()));

        // Wait function
        synchronized (this) {
            Thread.sleep((long) 2000);
        }
    }
}
