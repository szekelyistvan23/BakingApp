package com.stevensekler.baker.bakingapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Szekely Istvan on 24.03.2018.
 */

public class Cake implements Parcelable {

    private int id;
    private String name;
    private List<Ingredient> ingredients = null;
    private Step[] steps = null;
    private int servings;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.ingredients);
        dest.writeTypedArray(this.steps, flags);
        dest.writeInt(this.servings);
        dest.writeString(this.image);
    }

    public Cake() {
    }

    protected Cake(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        this.steps = in.createTypedArray(Step.CREATOR);
        this.servings = in.readInt();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Cake> CREATOR = new Parcelable.Creator<Cake>() {
        @Override
        public Cake createFromParcel(Parcel source) {
            return new Cake(source);
        }

        @Override
        public Cake[] newArray(int size) {
            return new Cake[size];
        }
    };
}
