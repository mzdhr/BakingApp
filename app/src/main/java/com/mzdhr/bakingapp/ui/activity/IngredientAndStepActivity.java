package com.mzdhr.bakingapp.ui.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.adapter.StepAdapter;
import com.mzdhr.bakingapp.helper.Constant;
import com.mzdhr.bakingapp.model.Recipe;
import com.mzdhr.bakingapp.widget.BakingAppWidgetProvider;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class IngredientAndStepActivity extends AppCompatActivity{

    // Objects
    private static String TAG = IngredientAndStepActivity.class.getSimpleName();

    // Views
    @BindView(R.id.ingredient_textView)
    public TextView mIngredientTextView;
    @BindView(R.id.item_list)
    public RecyclerView mStepsRecyclerView;
    @BindView(R.id.add_widget_button_imageView)
    public ImageView mAddWidgetButton;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        // Getting Recipe ArrayList from Intent
        if (getIntent().hasExtra(Constant.RECIPE_ARRAY_KEY)) {
            mRecipe = Parcels.unwrap(getIntent().getParcelableExtra(Constant.RECIPE_ARRAY_KEY));
            // Set Toolbar Title
            getSupportActionBar().setTitle(mRecipe.getName());
        }

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mAddWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWidget();
            }
        });

        // Populate Ingredient Values
        setupIngredientTextView();
        // Populate Steps Values
        setupRecyclerView();
    }

    private void addWidget() {
        // Generate Widget Text Data
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            result.append("- ");
            result.append(mRecipe.getIngredients().get(i).getIngredient());
            result.append(" (");
            result.append(mRecipe.getIngredients().get(i).getQuantity());
            result.append(" ");
            result.append(mRecipe.getIngredients().get(i).getMeasure());
            result.append(").");
            if (i != (mRecipe.getIngredients().size() - 1)) {
                result.append("\n");
            }
        }

        // Populate / Updating Widgets
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));
        if (appWidgetIds.length == 0) {
            Toast.makeText(this, "Please make a home screen widget first!", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < appWidgetIds.length; i++) {
                BakingAppWidgetProvider.updateAppWidget(this, appWidgetManager, appWidgetIds[i], mRecipe.getName() + " - Ingredients", result.toString());
                Toast.makeText(this, "Widget Added!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupIngredientTextView() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mRecipe.getIngredients().size(); i++) {
            result.append("- ");
            result.append(mRecipe.getIngredients().get(i).getIngredient());
            result.append(" (");
            result.append(mRecipe.getIngredients().get(i).getQuantity());
            result.append(" ");
            result.append(mRecipe.getIngredients().get(i).getMeasure());
            result.append(").");
            if (i != (mRecipe.getIngredients().size() - 1)) {
                result.append("\n");
            }
        }
        // Populate
        mIngredientTextView.setText(result.toString());
    }

    private void setupRecyclerView() {
        StepAdapter stepAdapter = new StepAdapter(this, mRecipe, mTwoPane);
        mStepsRecyclerView.setAdapter(stepAdapter);
        mStepsRecyclerView.setFocusable(false);
    }

    // Save data into saveInstanceState. So when user click back icon button.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constant.RECIPE_ARRAY_KEY, Parcels.wrap(mRecipe));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
