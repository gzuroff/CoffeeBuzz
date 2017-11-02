package com.gregoryzuroff.coffeebuzz;

import java.util.Hashtable;

/**
 * Created by gregoryzuroff on 11/2/17.
 */

public class User {
    String username, name, email;
    Hashtable<Drink, DrinkReview> drinkReviews;
    Hashtable<CoffeeShop, CoffeeShopReview> shopReviews;
    Boolean expert;
}
