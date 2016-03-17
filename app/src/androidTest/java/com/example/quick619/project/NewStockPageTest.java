package com.example.quick619.project;

import android.app.Application;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static com.example.quick619.project.CheckHint.withHint;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Sam on 3/17/2016
 *
 * This Espresso test is used for the following scenario test:
 *
 * Given the User is on the home screen
 * When he/she clicks the Add stock button
 * Then the screen shifts to another activity with user-inputted text fields for stock name
 * and upper and lower baselines as well as a spinner for choosing refresh rate.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewStockPageTest {

    private String topHint, botHint;
    private String spinHint, s1, s2, s3, s4, s5, s6, s7;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);


    // Initialize all strings that will be tested
    @Before
    public void initValidString() {
        topHint = "Top Baseline";
        botHint = "Bottom Baseline";
        spinHint = "Stock Refresh Rate";
        s1 = "5 minutes";
        s2 = "10 minutes";
        s3 = "30 minutes";
        s4 = "1 hour";
        s5 = "2 hours";
        s6 = "1 day";
        s7 = "1 week";
    }


    // Opens the NewStock from MainActivity before each test
    @Before
    public void openNewStockPage() {
        onView(withId(R.id.button3)).perform(click());
    }


    // Check if the SearchView exists
    @Test
    public void checkSearchViewExists() {
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
    }


    // Check if the baseline EditText views exist with the proper hints
    @Test
    public void checkBaselineHints() {
        onView(withId(R.id.upperThresh)).check(matches(withHint(topHint)));
        onView(withId(R.id.lowerThresh)).check(matches(withHint(botHint)));
    }


    // Check if the spinner exists with the proper spinner text
    @Test
    public void checkSpinnerText() {

        for (int i = 0; i < 8; i++) {

            // Opens the spinner
            onView(withId(R.id.notifyTime)).perform(click());
            String strToCheck = "";

            // Sets the proper string that will be checked, depending on the loop
            switch (i) {
                case 0:
                    strToCheck = spinHint;
                    break;
                case 1:
                    strToCheck = s1;
                    break;
                case 2:
                    strToCheck = s2;
                    break;
                case 3:
                    strToCheck = s3;
                    break;
                case 4:
                    strToCheck = s4;
                    break;
                case 5:
                    strToCheck = s5;
                    break;
                case 6:
                    strToCheck = s6;
                    break;
                case 7:
                    strToCheck = s7;
                    break;
            }

            // Clicks each spinner option and checks if it was selected correctly
            // http://stackoverflow.com/questions/31420839/android-espresso-check-selected-spinner-text
            onData(allOf(is(instanceOf(String.class)), is(strToCheck))).perform(click());
            onView(withId(R.id.notifyTime))
                    .check(matches(withSpinnerText(containsString(strToCheck))));
        }
    }
}
