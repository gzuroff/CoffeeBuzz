package com.gregoryzuroff.coffeebuzz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class HomePage extends Activity {
    private ListView coffeeList;
    private ArrayAdapter<String> listAdapter;
    // Get all shops data in shops
    public ArrayList<CoffeeShop> shops;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //initialize shops and listAdapter as empty
        shops = new ArrayList<CoffeeShop>();
        listAdapter = new ArrayAdapter<String>(HomePage.this, R.layout.simplerow);

        //Connect to Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Shops");


        // Read from the database
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Loops through all shops
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    //store shops
                    CoffeeShop temp = postSnapshot.getValue(CoffeeShop.class);
                    shops.add(temp);
                    //Add shop names to listAdapter
                    listAdapter.add(temp.name);
                }
                //Add shop names to coffeList ListView
                coffeeList = (ListView) findViewById(R.id.coffeeList);
                coffeeList.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        };
        myRef.addValueEventListener(postListener);
        Toast.makeText(this, Integer.toString(shops.size()), Toast.LENGTH_SHORT).show();
        for(CoffeeShop x : shops){
            Toast.makeText(this, x.name, Toast.LENGTH_SHORT).show();
        }
    }


}
