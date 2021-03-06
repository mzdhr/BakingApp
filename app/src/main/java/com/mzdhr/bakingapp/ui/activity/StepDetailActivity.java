package com.mzdhr.bakingapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.ui.fragment.StepDetailFragment;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Step;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link IngredientAndStepActivity}.
 */
public class StepDetailActivity extends AppCompatActivity{

    // Objects
    private static String TAG = StepDetailActivity.class.getSimpleName();
    private String mVideoUrl = "";
    private String mDescription = "";
    private int mCurrentStep = 0;
    ArrayList<Step> mSteps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(Constant.STEP_VIDEO_URL_KEY)) {
            mVideoUrl = getIntent().getStringExtra(Constant.STEP_VIDEO_URL_KEY);
            mDescription = getIntent().getStringExtra(Constant.STEP_DESCRIPTION_KEY);
            mSteps = Parcels.unwrap(getIntent().getParcelableExtra(Constant.STEP_LIST_KEY));
            mCurrentStep = getIntent().getIntExtra(Constant.CURRENT_STEP_KEY, 0);

            Log.d(TAG, "onCreate: mVideoURL: " + mVideoUrl);
            Log.d(TAG, "onCreate: mDescription: " + mDescription);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(Constant.STEP_VIDEO_URL_KEY, mVideoUrl);
            arguments.putString(Constant.STEP_DESCRIPTION_KEY, mDescription);
            arguments.putParcelable(Constant.STEP_LIST_KEY, Parcels.wrap(mSteps));
            arguments.putInt(Constant.CURRENT_STEP_KEY, mCurrentStep);

            arguments.putString(StepDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(StepDetailFragment.ARG_ITEM_ID));
            StepDetailFragment fragment = new StepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.item_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, IngredientAndStepActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
