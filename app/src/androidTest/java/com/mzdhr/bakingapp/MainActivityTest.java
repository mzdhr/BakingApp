package com.mzdhr.bakingapp;

import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mohammad on 17/03/2018.
 * This class test MainActivity.java UI
 * Test start after data retrieve from API, then it enter each item and check if its title correct.
 *
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {


    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);

    private MainActivity mMainActivity = null;
    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mMainActivity = mActivityTestRule.getActivity();

    }

    @Test
    public void titleTest01(){

    }


    @After
    public void unregisterIdlingResource(){
        // mMainActivity = null;
    }
}
