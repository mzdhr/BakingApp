package com.mzdhr.bakingapp.ui.activity;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

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
/**
 * Takes a String and returns it after a while via a callback.
 * <p>
 * This executes a long-running operation on a different thread that results in problems with
 * Espresso if an {@link IdlingResource} is not implemented and registered.
 */

/**
 * Created by mohammad on 19/03/2018.
 */

public class DataDownloader {

    private static final String TAG = DataDownloader.class.getSimpleName();

    private static final int DELAY_MILLIS = 3000;
    final static ArrayList<Recipe> mRecipes = new ArrayList<>();

    interface Callback {
        void onDone(ArrayList<Recipe> recipes);
    }

    static void requestDataByVolley(Context context, final Callback callback,
                                    @Nullable final DataDownloaderIdlingResource idlingResource) {
        /**
         * The IdlingResource is null in production as set by the @Nullable annotation which means
         * the value is allowed to be null.
         *
         * If the idle state is true, Espresso can perform the next action.
         * If the idle state is false, Espresso will wait until it is true before
         * performing the next action.
         */
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }


        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

        // Create RequestQueue object
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create JsonArray Request Object
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                mRecipes.clear();

                try {
                    for (int i = 0; i < response.length(); i++) {
                        jsonObject = response.getJSONObject(i);
                        Gson gson = new GsonBuilder().create();
                        Recipe recipe = gson.fromJson(jsonObject.toString(), Recipe.class);
                        mRecipes.add(recipe);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.d(TAG, "-----------------------------------");
                for (int i = 0; i < mRecipes.size(); i++) {
                    Recipe currentRecipe = mRecipes.get(i);
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


                /**
                 * {@link postDelayed} allows the {@link Runnable} to be run after the specified amount of
                 * time set in DELAY_MILLIS elapses. An object that implements the Runnable interface
                 * creates a thread. When this thread starts, the object's run method is called.
                 *
                 * After the time elapses, if there is a callback we return the image resource ID and
                 * set the idle state to true.
                 */


                callback.onDone(mRecipes);

                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });

        // Start that Request Object
        requestQueue.add(jsonArrayRequest);
    }
}