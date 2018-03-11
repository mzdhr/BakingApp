package com.mzdhr.bakingapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mzdhr.bakingapp.model.Ingredient;
import com.mzdhr.bakingapp.model.Recipe;
import com.mzdhr.bakingapp.model.Step;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Test ButterKnife Lib
    @BindView(R.id.recipeRecyclerView)
    RecyclerView mRecipeRecyclerView;
    @BindString(R.string.app_name)
    String mAppName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        // Test ButterKnife Lib
        //mTitleTextView.setText(mAppName);
        // Test Volley Lib
        requestByVolley();
        // Test GSON Lib

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void requestByVolley() {
        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        // Create RequestQueue object
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JsonArray Request Object
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //mTitleTextView.setText("Response: " + response.toString());
                // notifyDataSetChanged(;
                // progress.dismiss(;

                JSONObject jsonObject = null;
                ArrayList<Recipe> recipes = new ArrayList<>();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        Recipe recipe = gson.fromJson(jsonObject.toString(), Recipe.class);
                        recipes.add(recipe);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d(TAG, "-----------------------------------");
                for (int i = 0; i < recipes.size(); i++) {
                    Recipe currentRecipe = recipes.get(i);
                    Log.d(TAG, "* Recipe ID: " + currentRecipe.getId());
                    Log.d(TAG, "* Recipe Name: " + currentRecipe.getName());
                    Log.d(TAG, "* Recipe Servings: " + currentRecipe.getServings());
                    Log.d(TAG, "* Recipe Image: " + currentRecipe.getImage());
                    Log.d(TAG, "* Recipe Ingredients: ");

                    for (int j = 0; j < currentRecipe.getIngredients().size(); j++) {
                        Ingredient currentIngredient = currentRecipe.getIngredients().get(j);
                        String quantity = currentIngredient.getQuantity();
                        String measure = currentIngredient.getMeasure();
                        String ingredient = currentIngredient.getIngredient();
                        Log.d(TAG, "  - " + ingredient + " (" + quantity + " " + measure + ").");
                    }

                    Log.d(TAG, "* Recipe Steps: ");
                    for (int j = 0; j < currentRecipe.getSteps().size(); j++) {
                        Step currentStep = currentRecipe.getSteps().get(j);
                        String id = currentStep.getId();
                        String shortDescription = currentStep.getShortDescription();
                        String description = currentStep.getDescription();
                        String videoURL = currentStep.getVideoURL();
                        String thumbnailURL = currentStep.getThumbnailURL();
                        Log.d(TAG, "  - " + id + ". " + shortDescription);
                        Log.d(TAG, "    - Description: " + description);
                        Log.d(TAG, "    - VideoURL: " + videoURL);
                        Log.d(TAG, "    - ThumbnailURL: " + thumbnailURL);
                    }
                    Log.d(TAG, "-----------------------------------");

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        // Start that Request Object
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
