package com.mzdhr.bakingapp.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mzdhr.bakingapp.R;
import com.mzdhr.bakingapp.adapter.RecipeAdapter;
import com.mzdhr.bakingapp.model.Recipe;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DataDownloader.Callback {
    // Object
    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<Recipe> mRecipes = new ArrayList<>();
    private RecipeAdapter mRecipeAdapter;

    // Views
    @BindView(R.id.recipeRecyclerView)
    public RecyclerView mRecipeRecyclerView;
    @BindString(R.string.app_name)
    public String mAppName;

    // For Testing Espresso
    @Nullable
    public DataDownloaderIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public DataDownloaderIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new DataDownloaderIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIdlingResource();
        DataDownloader.requestDataByVolley(this, MainActivity.this, mIdlingResource);
    }

    @Override
    public void onDone(ArrayList<Recipe> recipes) {
        mRecipes = recipes;
        setRecyclerView();
        setAdapter();
    }

    private void setRecyclerView() {
        // Determined if Table or Phone by using is_tablet.xml (one for normal, one for screen above w900dp) in values directory.
        if (getResources().getBoolean(R.bool.isTablet)) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mRecipeRecyclerView.setLayoutManager(gridLayoutManager);
            Log.d(TAG, "onCreate: It is a Tablet");
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mRecipeRecyclerView.setLayoutManager(linearLayoutManager);
            Log.d(TAG, "onCreate: It is a Phone");
        }
        mRecipeRecyclerView.setHasFixedSize(true);
    }

    private void setAdapter() {
        mRecipeAdapter = new RecipeAdapter(mRecipes, this);
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_settings) {
            DataDownloader.requestDataByVolley(this, MainActivity.this, mIdlingResource);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
