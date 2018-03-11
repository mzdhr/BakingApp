package com.mzdhr.bakingapp.model;

import java.util.ArrayList;

/**
 * Created by mohammad on 11/03/2018.
 * Building this class with GSON Lib
 * The field names must match (including the case) with the names in JSON.
 * the class must include a default constructor, even if it is private
 */

public class Recipe {
    private String id;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private Integer servings;
    private String image;

    private Recipe() {
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public Integer getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }
}
