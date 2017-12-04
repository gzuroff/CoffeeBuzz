package com.gregoryzuroff.coffeebuzz;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static android.content.ContentValues.TAG;

public class HomePage extends Activity implements Button.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private ListView coffeeList;
    private ArrayAdapter<String> adapter;
    // Get all shops data in shops
    private ArrayList<CoffeeShop> shops = new ArrayList<>();
    private Button buttonSort;
    private Button buttonFilter;
    private Boolean sortName = true;

    private ArrayList<String> filters;

    private String clickedShopName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //initialize shops and listAdapter as empty
        coffeeList = findViewById(R.id.coffeeList);
        buttonSort = findViewById(R.id.sortButton);
        buttonFilter = findViewById(R.id.filterMenu);

        filters = new ArrayList<>();

        buttonSort.setOnClickListener(this);
        buttonFilter.setOnClickListener(this);
        fetchData(sortName, filters);
    }

    @Override
    public void onClick(View view){
        if(view == buttonSort){
            showPopup(view);
        }
        else if(view == buttonFilter){
            showPopupMenu(view);
        }
    }

    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.filter, popup.getMenu());
        popup.show();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.sort, popup.getMenu());
        popup.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nameSort:
                sortName = true;
                fetchData(sortName, filters);
                return true;
            case R.id.ratingSort:
                sortName = false;
                fetchData(sortName, filters);
                return true;
            case R.id.filterCoffee:
                filters.add("coffee");
                return true;
            case R.id.filterEspresso:
                filters.add("espresso");
                return true;
            case R.id.filterCappuccino:
                filters.add("cappuccino");
                return true;
            default:
                return false;
        }
    }
    public void fetchData(Boolean sorted, final ArrayList<String> filters1){
        shops.clear();
        coffeeList.setAdapter(null);
        shops = new ArrayList<>();
        sortName = sorted;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Shops");
        // Read from the database
        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    CoffeeShop shop = postSnapshot.getValue(CoffeeShop.class);
                    shops.add(shop);
                    Toast.makeText(HomePage.this, shop.menu.get("coffee").classification, Toast.LENGTH_SHORT).show();
                }
                final ArrayList<Integer> indexes = new ArrayList<>();
                if (!filters1.isEmpty()) {
                    Drink[] drinkList;
                    for (int i = 0; i < shops.size(); i++){
                        drinkList = shops.get(i).menu.values().toArray(new Drink[0]);
                        for(int j = 0; j < drinkList.length; j++){
                            if( filters1.contains(drinkList[j].classification)){
                                indexes.add(i);
                                break;
                            }
                        }
                    }
                }
                else{
                    for (int i = 0; i < shops.size(); i++){
                        indexes.add(i);
                    }
                }
                if(indexes.size() < 1){
                    shops.clear();
                    coffeeList.setAdapter(null);
                    shops = new ArrayList<>();
                    return;
                }

                if(sortName){
                    Collections.sort(shops, new Comparator<CoffeeShop>() {
                        @Override
                        public int compare(CoffeeShop coffeeShop, CoffeeShop t1) {
                            return coffeeShop.name.compareToIgnoreCase(t1.name);
                        }
                    });
                }
                else{
                    Collections.sort(shops, new Comparator<CoffeeShop>() {
                        @Override
                        public int compare(CoffeeShop coffeeShop, CoffeeShop t1) {
                            return (int) (t1.avgOverall - coffeeShop.avgOverall);
                        }
                    });
                }
                adapter = new ArrayAdapter(HomePage.this, R.layout.simplerow, R.id.textViewShopName, shops){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textViewShopName = view.findViewById(R.id.textViewShopName);
                        RatingBar ratingBarShopRating = view.findViewById(R.id.ratingBarShopRating);
                        if(indexes.contains(position)) {
                            textViewShopName.setText(shops.get(position).name);
                            ratingBarShopRating.setRating((float) shops.get(position).avgOverall);
                            ratingBarShopRating.setIsIndicator(true);
                        }
                        return view;
                    }
                };
                coffeeList.setAdapter(adapter);
                coffeeList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                        clickedShopName = shops.get(i).name;
                        Intent shopPage = new Intent(HomePage.this, CoffeeShopActivity.class);
                        shopPage.putExtra("shop", shops.get(i).name);
                        HomePage.this.startActivity(shopPage);
                    }});

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }

        };
        myRef.addValueEventListener(postListener);
    }

}
