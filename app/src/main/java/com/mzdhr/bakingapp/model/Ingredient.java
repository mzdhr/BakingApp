package com.mzdhr.bakingapp.model;

/**
 * Created by mohammad on 11/03/2018.
 */

public class Ingredient {
    private String quantity;
    private String measure;
    private String ingredient;

    private Ingredient() {
    }

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }
}
