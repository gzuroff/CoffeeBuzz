package com.gregoryzuroff.coffeebuzz;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by gregoryzuroff on 11/2/17.
 */

public class CoffeeShop {
    int id;
    int noiseLevel;
    double avgOverall;
    double avgVariety;
    double avgStudying;
    double avgLight;
    double avgAccess;
    String name;
    String atmosphere;
    double longitude, latitude;
    double[] gps = new double[2];

    public void setLongitude(double longitude){
        this.longitude = longitude;
        this.gps[0] = this.longitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
        this.gps[1] = this.latitude;
    }

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


    public void setMenu(Map<String, Drink> menu) {
        this.menu = menu;
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
    }

    Map<String, Drink> menu;
    //Hashtable<String, ArrayList<CoffeeShopReview>> reviews;


}
