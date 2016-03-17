package com.example.quick619.project;

import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;


/**
 * Created by Sam on 3/17/2016
 *
 * Used to check the hint texts in EditText views
 *
 * Credit to Eduard K. and Snicolas
 * http://stackoverflow.com/questions/20334455/android-espresso-how-to-check-edittext-hint
 */

public class CheckHint {

    public static Matcher<View> withHint(final String expectedHint) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }

                String hint = ((EditText) view).getHint().toString();

                return expectedHint.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}


