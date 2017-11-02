package com.gregoryzuroff.coffeebuzz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by gregoryzuroff on 11/2/17.
 */

public class CoffeeShop {
    int ID, noiseLevel;
    double avgOverall, avgVariety, avgStudying, avgLight, avgAccess;
    String name, atmosphere;
    double[] gps = new double[2];
    ArrayList<Drink> menu;
    Hashtable<String, ArrayList<CoffeeShopReview>> reviews;
}
