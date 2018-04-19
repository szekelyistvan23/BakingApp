package com.stevensekler.baker.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
//    Based on : https://github.com/chiuki/espresso-samples
    @Test
    public void checkTitle(){
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
        .check(matches(withText(R.string.cakes_title)));
    }

    @Test
    public void checkCakeName(){
        onView(withId(R.id.cake_recycler_view))
                .perform(actionOnItemAtPosition(1, click()));

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(R.string.brownies)));

    }

    @Test
    public void scrollToPosition(){
        onView(withId(R.id.cake_recycler_view)).perform(RecyclerViewActions.scrollToPosition(3));
    }

}