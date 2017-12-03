package com.gregoryzuroff.coffeebuzz;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gregoryzuroff on 11/2/17.
 */

public class Drink {
    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setAvgRating(int avgRating) {
        this.avgRating = avgRating;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    String classification, name;

    public void setStrength(int strength) {
        this.strength = strength;
    }

    int strength, avgRating;
    double price;
    //Hashtable<String, ArrayList<DrinkReview>> Reviews;
}
