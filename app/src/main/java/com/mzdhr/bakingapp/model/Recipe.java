package com.mzdhr.bakingapp.model;

import org.parceler.Parcel;
import java.util.ArrayList;

/**
 * Created by mohammad on 11/03/2018.
 * Building this class with GSON Lib
 * The field names must match (including the case) with the names in JSON.
 * the class must include a default constructor, even if it is private
 */

@Parcel
public class Recipe {
    String id;
    String name;
    ArrayList<Ingredient> ingredients;
    ArrayList<Step> steps;
    Integer servings;
    String image;

    public Recipe(){

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
