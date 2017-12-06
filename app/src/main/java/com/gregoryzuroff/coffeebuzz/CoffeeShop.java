package com.gregoryzuroff.coffeebuzz;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;


public class CoffeeShop {
    //id isnt really necessary until we start adding multiple of the same shops
    //i.e. the multiple espresso royales on campus...
    int id, noiseLevel, votes;
    double avgOverall, avgVariety, avgStudying, avgLight, avgAccess, longitude, latitude;
    String name, atmosphere;
    Boolean hasDrinks;

    public void setVotes(int votes) { this.votes = votes; }

    public void setHasDrinks(Boolean hasDrinks){ this.hasDrinks = hasDrinks; }

    public void setLongitude(double longitude){ this.longitude = longitude; }

    public void setLatitude(double latitude){ this.latitude = latitude; }

    public void setId(int id) {
        this.id = id;
    }

    public void setNoiseLevel(int noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public void setAvgOverall(double avgOverall) {
        this.avgOverall = avgOverall;
    }

    public void setAvgVariety(double avgVariety) {
        this.avgVariety = avgVariety;
    }

    public void setAvgStudying(double avgStudying) {
        this.avgStudying = avgStudying;
    }

    public void setAvgLight(double avgLight) {
        this.avgLight = avgLight;
    }

    public void setAvgAccess(double avgAccess) {
        this.avgAccess = avgAccess;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAtmosphere(String atmosphere) {
        this.atmosphere = atmosphere;
    }

    public CoffeeShop(){}

    public CoffeeShop(int noiseLevel, double avgOverall, double avgVariety, double avgStudying, double avgLight, double avgAccess, String name, String atmosphere) {
        this.noiseLevel = noiseLevel;
        this.avgOverall = avgOverall;
        this.avgVariety = avgVariety;
        this.avgStudying = avgStudying;
        this.avgLight = avgLight;
        this.avgAccess = avgAccess;
        this.name = name;
        this.atmosphere = atmosphere;
        this.hasDrinks = false;
        this.votes = 1;
    }

    public void setMenu(HashMap<String, Drink> menu) { this.menu = menu; }

    //Made menu a hashmap instead of a map, thinking it would fix an error
    //error was unrelated, but don't want to risk changing it back...
    HashMap<String, Drink> menu;
    //Hashtable<String, ArrayList<CoffeeShopReview>> reviews;


}
