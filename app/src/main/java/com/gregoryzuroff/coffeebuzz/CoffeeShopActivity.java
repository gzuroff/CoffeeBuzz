package com.gregoryzuroff.coffeebuzz;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CoffeeShopActivity extends Activity implements Button.OnClickListener {

    private String chosenShopName;
    private CoffeeShop shop;
    private TextView textViewName, textViewNoise, textViewVariety, textViewStudying, textViewLight,
    textViewAccess, textViewAtmosphere, textViewRating;
    private RatingBar ratingBarOverall;
    private ListView listViewMenu;
    ArrayAdapter<String> adapter;
    private Drink[] drinkNames;

    private Button buttonAddDrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_shop);

        textViewName = (TextView) findViewById(R.id.textViewShopName);
        textViewNoise = (TextView) findViewById(R.id.textViewNoise);
        textViewVariety = (TextView) findViewById(R.id.textViewVariety);
        textViewStudying = (TextView) findViewById(R.id.textViewStudying);
        textViewLight = (TextView) findViewById(R.id.textViewLight);
        textViewAccess = (TextView) findViewById(R.id.textViewAccess);
        textViewAtmosphere = (TextView) findViewById(R.id.textViewAtmosphere);
        textViewRating = (TextView) findViewById(R.id.textViewRating);

        ratingBarOverall = (RatingBar) findViewById(R.id.ratingBarOverall);

        buttonAddDrink = (Button) findViewById(R.id.buttonAddDrink);
        buttonAddDrink.setOnClickListener(this);

        listViewMenu = (ListView) findViewById(R.id.listViewMenu);

        chosenShopName = getIntent().getStringExtra("shop");

        fetchData();

    }
    @Override
    public void onClick(View view){
        if(view == buttonAddDrink) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
            dialog.setTitle("Add Drink");

            Context context = view.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText nameBox = new EditText(context);
            nameBox.setHint("Name");
            layout.addView(nameBox);

            final TextView ratingText = new TextView(context);
            ratingText.setText("Rating:");
            layout.addView(ratingText);

            final RatingBar ratingBox = new RatingBar(context);
            ratingBox.setMax(5);
            ratingBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(ratingBox);

            final EditText classificationBox = new EditText(context);
            classificationBox.setHint("Classification");
            layout.addView(classificationBox);

            final EditText priceBox = new EditText(context);
            priceBox.setHint("Price");
            layout.addView(priceBox);

            final EditText strengthBox = new EditText(context);
            strengthBox.setHint("Strength");
            layout.addView(strengthBox);

            final Button buttonAdd = new Button(context);
            buttonAdd.setText("Add Drink");
            layout.addView(buttonAdd);

            dialog.setView(layout);
            dialog.create();
            dialog.show();
            buttonAdd.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view == buttonAdd){
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("Shops/" + chosenShopName);

                        Drink newDrink = new Drink(classificationBox.getText().toString(), nameBox.getText().toString(),
                                Integer.parseInt(strengthBox.getText().toString()), ratingBox.getRating(),
                                Double.parseDouble(priceBox.getText().toString()));
                        myRef.child("menu").child(newDrink.name).setValue(newDrink);
                        myRef.child("hasDrinks").setValue(true);
                        fetchData();
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.goback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.goToHomePage:
                startActivity(new Intent(getApplicationContext(), HomePage.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void fetchData(){
        shop = new CoffeeShop();
        listViewMenu.setAdapter(null);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Shops/" + chosenShopName);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                shop = dataSnapshot.getValue(CoffeeShop.class);

                textViewName.setText("Shop: " + shop.name);
                textViewNoise.setText("Noise Level: " +  Integer.toString(shop.noiseLevel));
                textViewVariety.setText("Variety: " + Double.toString(shop.avgVariety));
                textViewStudying.setText("Good For Studying: " + Double.toString(shop.avgStudying));
                textViewLight.setText("Light Quality: " + Double.toString(shop.avgLight));
                textViewAccess.setText("Accessability: " + Double.toString(shop.avgAccess));
                textViewAtmosphere.setText("Atmoshpere: " + shop.atmosphere);
                textViewRating.setText("Rating:");

                ratingBarOverall.setIsIndicator(true);
                ratingBarOverall.setRating((float) shop.avgOverall);
                if (shop.hasDrinks) {
                    //drinkNames = new ArrayList<>();
                    drinkNames = shop.menu.values().toArray(new Drink[0]);

                    adapter = new ArrayAdapter(CoffeeShopActivity.this, R.layout.menurow, R.id.textViewDrinkName, drinkNames) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView textViewDrinkName = view.findViewById(R.id.textViewDrinkName);
                            RatingBar ratingBarDrinkRating = view.findViewById(R.id.ratingBarDrinkRating);

                            textViewDrinkName.setText(drinkNames[position].name);
                            ratingBarDrinkRating.setRating((float) drinkNames[position].avgRating);

                            return view;
                        }
                    };
                    listViewMenu.setAdapter(adapter);
                }
                else{
                    listViewMenu.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
