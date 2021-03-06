package com.stevensekler.baker.bakingapp;

/**
 *  Makes Espresso tests for MainActivity and FragmentsActivity.
 */

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    public static final int BROWNIES_POSITION = 1;
    public static final int CHEESECAKE_POSITION = 3;
    @Rule
    public final ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);
//    Based on : https://github.com/chiuki/espresso-samples
    /** Checks Main Activity's title */
    @Test
    public void checkTitle(){
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
        .check(matches(withText(R.string.cakes_title)));
    }
    /** Checks the title of FragmentsActivity after a click in MainActivity */
    @Test
    public void checkCakeName(){
        onView(withId(R.id.cake_recycler_view))
                .perform(actionOnItemAtPosition(BROWNIES_POSITION, click()));

        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(R.string.brownies)));

    }

    /** Scrolls RecyclerView to the cheesecake's position*/
    @Test
    public void scrollToPosition(){
        onView(withId(R.id.cake_recycler_view)).perform(RecyclerViewActions.scrollToPosition(CHEESECAKE_POSITION));
    }

}