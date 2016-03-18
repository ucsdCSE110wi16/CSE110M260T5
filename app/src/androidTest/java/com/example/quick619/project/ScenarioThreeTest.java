package com.example.quick619.project;

import android.content.ClipData;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
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
    public void checkStockTest () {
        onData(instanceOf(ClipData.Item.class))
                .inAdapterView(allOf(withId(R.id.stockList), isDisplayed()))
                .atPosition(0)
                .check(matches(isDisplayed()));
    }


}
