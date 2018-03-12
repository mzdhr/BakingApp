package com.mzdhr.bakingapp.model;

import org.parceler.Parcel;

/**
 * Created by mohammad on 11/03/2018.
 */
@Parcel
public class Ingredient {
    String quantity;
    String measure;
    String ingredient;

    public Ingredient() {
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
