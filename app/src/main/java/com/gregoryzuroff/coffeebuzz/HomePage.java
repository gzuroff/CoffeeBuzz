package com.gregoryzuroff.coffeebuzz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private ArrayList<CoffeeShop> shops1 = new ArrayList<>();
    private Button buttonSort;
    private Button buttonFilter;
    private Button buttonAddShop;
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
        buttonAddShop = findViewById(R.id.buttonAddShop);

        filters = new ArrayList<>();

        buttonSort.setOnClickListener(this);
        buttonFilter.setOnClickListener(this);
        buttonAddShop.setOnClickListener(this);
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
        else if(view == buttonAddShop){
            final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
            dialog.setTitle("Add Coffee Shop");

            Context context = view.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText nameBox = new EditText(context);
            nameBox.setHint("Name of Coffee Shop");
            layout.addView(nameBox);

            final TextView ratingText = new TextView(context);
            ratingText.setText("Rating:");
            layout.addView(ratingText);

            final RatingBar ratingBox = new RatingBar(context);
            ratingBox.setMax(5);
            ratingBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(ratingBox);

            final EditText accessBox = new EditText(context);
            accessBox.setHint("Accessibility");
            accessBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(accessBox);

            final EditText atmosphereBox = new EditText(context);
            atmosphereBox.setHint("Atmosphere");
            layout.addView(atmosphereBox);

            final EditText lightBox = new EditText(context);
            lightBox.setHint("Light Quality");
            lightBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(lightBox);

            final EditText studyBox = new EditText(context);
            studyBox.setHint("Ability To Study");
            studyBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(studyBox);

            final EditText varietyBox = new EditText(context);
            varietyBox.setHint("Variety");
            varietyBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(varietyBox);

            final EditText noiseBox = new EditText(context);
            noiseBox.setHint("Noise Level");
            noiseBox.setInputType(InputType.TYPE_CLASS_NUMBER);
            layout.addView(noiseBox);

            final Button buttonAdd = new Button(context);
            buttonAdd.setText("Add Coffee Shop");
            layout.addView(buttonAdd);

            dialog.setView(layout);
            dialog.create();
            dialog.show();
            buttonAdd.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view == buttonAdd){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Shops/" + nameBox.getText().toString());
                        CoffeeShop newShop = new CoffeeShop(
                                Integer.parseInt(noiseBox.getText().toString()),
                                ratingBox.getRating(),
                                Double.parseDouble(varietyBox.getText().toString()),
                                Double.parseDouble(studyBox.getText().toString()),
                                Double.parseDouble(lightBox.getText().toString()),
                                Double.parseDouble(accessBox.getText().toString()),
                                nameBox.getText().toString(),
                                atmosphereBox.getText().toString()

                        );
                        myRef.setValue(newShop);
                    }
                }
            });
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
                fetchData(sortName, filters);
                return true;
            case R.id.filterEspresso:
                filters.add("espresso");
                fetchData(sortName, filters);
                return true;
            case R.id.filterCappuccino:
                filters.add("cappuccino");
                fetchData(sortName, filters);
                return true;
            case R.id.filterOther:
                filters.add("other");
                fetchData(sortName, filters);
                return true;
            case R.id.filterClear:
                filters.clear();
                filters = new ArrayList<>();
                fetchData(sortName, filters);
                return true;
            default:
                return false;
        }
    }
    public void fetchData(Boolean sorted, final ArrayList<String> filters1){
        shops.clear();
        shops1.clear();
        coffeeList.setAdapter(null);
        shops = new ArrayList<>();
        shops1 = new ArrayList<>();
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
                }
                final ArrayList<Integer> indexes = new ArrayList<>();
                if (!filters1.isEmpty()) {
                    Drink[] drinkList;
                    for (int i = 0; i < shops.size(); i++){
                        drinkList = shops.get(i).menu.values().toArray(new Drink[0]);
                        for(int j = 0; j < drinkList.length; j++){
                            if( filters1.contains(drinkList[j].classification)){
                                if(!shops1.contains(shops.get(i))) {
                                    indexes.add(i);
                                    shops1.add(shops.get(i));
                                }
                                break;
                            }
                        }
                    }
                }
                else{
                    for (int i = 0; i < shops.size(); i++){
                        indexes.add(i);
                        shops1.add(shops.get(i));
                    }
                }
                if(indexes.size() < 1){
                    shops.clear();
                    coffeeList.setAdapter(null);
                    shops = new ArrayList<>();
                    return;
                }

                if(sortName){
                    Collections.sort(shops1, new Comparator<CoffeeShop>() {
                        @Override
                        public int compare(CoffeeShop coffeeShop, CoffeeShop t1) {
                            return coffeeShop.name.compareToIgnoreCase(t1.name);
                        }
                    });
                }
                else{
                    Collections.sort(shops1, new Comparator<CoffeeShop>() {
                        @Override
                        public int compare(CoffeeShop coffeeShop, CoffeeShop t1) {
                            return (int) (t1.avgOverall - coffeeShop.avgOverall);
                        }
                    });
                }
                adapter = new ArrayAdapter(HomePage.this, R.layout.simplerow, R.id.textViewShopName, shops1){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textViewShopName = view.findViewById(R.id.textViewShopName);
                        RatingBar ratingBarShopRating = view.findViewById(R.id.ratingBarShopRating);
                        textViewShopName.setText(shops1.get(position).name);
                        ratingBarShopRating.setRating((float) shops1.get(position).avgOverall);
                        ratingBarShopRating.setIsIndicator(true);

                        return view;
                    }
                };
                coffeeList.setAdapter(adapter);
                coffeeList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                        clickedShopName = shops1.get(i).name;
                        Intent shopPage = new Intent(HomePage.this, CoffeeShopActivity.class);
                        shopPage.putExtra("shop", shops1.get(i).name);
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
