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
import android.widget.AdapterView;
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

    private Button buttonAddDrink, buttonAddShopRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_shop);
        //These don't need to be casted as their type...
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
        buttonAddShopRating = findViewById(R.id.buttonAddShopRating);
        buttonAddShopRating.setOnClickListener(this);

        listViewMenu = (ListView) findViewById(R.id.listViewMenu);

        chosenShopName = getIntent().getStringExtra("shop");

        fetchData();

    }
    @Override
    public void onClick(final View view){
        if(view == buttonAddDrink) {
            //first level dialog box
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
        else if(view == buttonAddShopRating){
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Shops/" + chosenShopName);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //second level dialog box
                    final CoffeeShop tempShop = dataSnapshot.getValue(CoffeeShop.class);
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                    dialog.setTitle("Rate " + tempShop.name);

                    Context context = view.getContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final TextView ratingText = new TextView(context);
                    ratingText.setText("Rating:");
                    layout.addView(ratingText);

                    final RatingBar ratingBox = new RatingBar(context);
                    ratingBox.setMax(5);
                    ratingBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(ratingBox);

                    final Button buttonDone = new Button(context);
                    buttonDone.setText("Add Rating!");
                    layout.addView(buttonDone);

                    dialog.setView(layout);
                    dialog.create();
                    dialog.show();
                    buttonDone.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(view == buttonDone){
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference newRef = database.getReference("Shops/" + chosenShopName);
                                int count = tempShop.votes;
                                float rating = ratingBox.getRating();

                                float newRating = (float) tempShop.avgOverall * count + rating;
                                count++;
                                newRating /= (float) count;

                                myRef.child("avgOverall").setValue(newRating);
                                myRef.child("votes").setValue(count);
                                fetchData();
                            }
                        }
                    });

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
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
//Fetches data -- called many times, called recursively as well
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
                    listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l){
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference myRef = database.getReference("Shops/" + chosenShopName);
                            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                //Greg contributed to 1,344 lines of this project. Nobody else contributed
                                //to the code... If you see this, please say something so I know you at least read it
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1){
                                    final int x = i;

                                    final String clickedDrinkName;
                                    clickedDrinkName =drinkNames[x].name;
                                    final Drink newDrink = dataSnapshot1.child("menu").child(clickedDrinkName).getValue(Drink.class);

                                    final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                                    dialog.setTitle("Rate "+ clickedDrinkName);

                                    Context context = view.getContext();
                                    LinearLayout layout = new LinearLayout(context);
                                    layout.setOrientation(LinearLayout.VERTICAL);


                                    final TextView classBox = new TextView(context);
                                    classBox.setText("Classification: "+ newDrink.classification);
                                    layout.addView(classBox);

                                    final TextView ratingText = new TextView(context);
                                    ratingText.setText("Rating: ");
                                    layout.addView(ratingText);

                                    final RatingBar ratingBox = new RatingBar(context);
                                    ratingBox.setMax(5);
                                    ratingBox.setIsIndicator(true);
                                    ratingBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                                    ratingBox.setRating((float)drinkNames[i].avgRating);
                                    layout.addView(ratingBox);

                                    final TextView priceBox = new TextView(context);
                                    priceBox.setText("Price: "+ newDrink.price);
                                    layout.addView(priceBox);

                                    final TextView strengthBox = new TextView(context);
                                    strengthBox.setText("Strength: "+ newDrink.strength);
                                    layout.addView(strengthBox);


                                    final Button buttonAddDrinkRating = new Button(context);
                                    buttonAddDrinkRating.setText("Add Rating!");
                                    layout.addView(buttonAddDrinkRating);

                                    dialog.setView(layout);
                                    dialog.create();
                                    dialog.show();
                                    buttonAddDrinkRating.setOnClickListener(new Button.OnClickListener(){
                                        @Override
                                        public void onClick (View view){
                                        if (view == buttonAddDrinkRating) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            final DatabaseReference newRef = database.getReference("Shops/" + chosenShopName + "/menu/" + clickedDrinkName);

                                            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(view.getContext());
                                            dialog.setTitle("Rate " + clickedDrinkName + "!");

                                            Context context1 = view.getContext();
                                            LinearLayout layout1 = new LinearLayout(context1);
                                            layout1.setOrientation(LinearLayout.VERTICAL);

                                            final TextView ratingText1 = new TextView(context1);
                                            ratingText1.setText("Rating:");
                                            layout1.addView(ratingText1);

                                            final RatingBar ratingBox1 = new RatingBar(context1);
                                            ratingBox1.setMax(5);
                                            ratingBox1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                            layout1.addView(ratingBox1);

                                            final Button buttonDone1 = new Button(context1);
                                            buttonDone1.setText("Add Rating!");
                                            layout1.addView(buttonDone1);

                                            dialog1.setView(layout1);
                                            dialog1.create();
                                            dialog1.show();

                                            buttonDone1.setOnClickListener(new Button.OnClickListener(){
                                                @Override
                                                public void onClick(View view) {
                                                    int count = newDrink.votes;
                                                    float rating = ratingBox1.getRating();

                                                    float newRating = (float) newDrink.avgRating * count + rating;
                                                    count++;
                                                    newRating /= (float) count;

                                                    newRef.child("avgRating").setValue(newRating);
                                                    newRef.child("votes").setValue(count);
                                                    fetchData();
                                                }
                                            });

                                        }
                                    }
                                    });
                            }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }});
                            }
                        });
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
