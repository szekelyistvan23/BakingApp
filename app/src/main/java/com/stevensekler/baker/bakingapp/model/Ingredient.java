package com.stevensekler.baker.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores the ingredients of a cake.
 */

public class Ingredient implements Parcelable {

    private double quantity;
    private String measure;
    private String ingredient;

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.quantity);
        dest.writeString(this.measure);
        dest.writeString(this.ingredient);
    }

    public Ingredient() {
    }

    protected Ingredient(Parcel in) {
        this.quantity = in.readDouble();
        this.measure = in.readString();
        this.ingredient = in.readString();
    }

    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    /** Custom method to display the ingredients.*/
    @Override
    public String toString() {
        int result = 0;
        if (quantity % 1 == 0){
            result = (int)quantity;
            return  result + " " +
                    measure + " " +
                    ingredient;
        } else {
            return  quantity + " " +
                    measure + " " +
                    ingredient;
        }
    }
}
