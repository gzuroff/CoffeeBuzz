package com.gregoryzuroff.coffeebuzz;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gregoryzuroff on 11/2/17.
 */

public class Drink {
    String classifcation, name;
    int strenght, avgRating;
    double price;
    Hashtable<String, ArrayList<DrinkReview>> Reviews;
}
