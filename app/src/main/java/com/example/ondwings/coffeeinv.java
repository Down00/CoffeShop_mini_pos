package com.example.ondwings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class coffeeinv extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText productnameEditText, pricetallEditText, pricegrandeEditText;
    private Button saveButton, backButton;
    private CheckBox addonsCheckBox;
    private LinearLayout addonsContainer, addonsListContainer;
    private Button addNewAddonButton;
    private List<View> addonViews = new ArrayList<>();

    private String[] categories = {"Select Category", "ICED COFFEE", "HOT DRINK", "SPECIAL DRINK", "FRUITEAS", "NON-COFFEE ( HOT )", "COFFEE ( HOT )"};

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_coffee_activity);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        productnameEditText = findViewById(R.id.productnameEditText);
        pricetallEditText = findViewById(R.id.pricetallEditText);
        pricegrandeEditText = findViewById(R.id.pricegrandeEditText);
        addonsCheckBox = findViewById(R.id.addonsCheckBox);
        addonsContainer = findViewById(R.id.addonsContainer);
        addonsListContainer = findViewById(R.id.addonsListContainer);
        addNewAddonButton = findViewById(R.id.addNewAddonButton);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        Button switchh = findViewById(R.id.swithtomenuEditText);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Coffee");

        // Set up the category spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Handle add-ons visibility
        addonsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            addonsContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                // Clear all add-ons when disabled
                addonsListContainer.removeAllViews();
                addonViews.clear();
            }
        });

        // Handle add new add-on button
        addNewAddonButton.setOnClickListener(v -> addNewAddon());

        // Set up save button
        saveButton.setOnClickListener(v -> saveProduct());

        switchh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(coffeeinv.this, menuinv.class));
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to MainActivity
                startActivity(new Intent(coffeeinv.this, coffeeprd.class));
            }
        });
    }

    private void addNewAddon() {
        View addonView = getLayoutInflater().inflate(R.layout.addon_item, addonsListContainer, false);

        // Set up delete button
        ImageButton deleteButton = addonView.findViewById(R.id.deleteAddonButton);
        deleteButton.setOnClickListener(v -> {
            addonsListContainer.removeView(addonView);
            addonViews.remove(addonView);
        });

        // Add to container and list
        addonsListContainer.addView(addonView);
        addonViews.add(addonView);
    }

    private void saveProduct() {
        String productname = productnameEditText.getText().toString().trim();
        String pricetall = pricetallEditText.getText().toString().trim();
        String pricegrande = pricegrandeEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (productname.isEmpty() || pricetall.isEmpty() || pricegrande.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Menu");
        String id = databaseReference.push().getKey();

        if (id != null) {
            coffeeItem item = new coffeeItem();
            item.setProductcode(category);
            item.setProductname(productname);
            item.setPricetall(pricetall);
            item.setPricegrande(pricegrande);

            // Handle add-ons
            if (addonsCheckBox.isChecked() && !addonViews.isEmpty()) {
                item.setHasAddons(true);
                List<Addon> addons = new ArrayList<>();

                for (View addonView : addonViews) {
                    CheckBox addonCheckBox = addonView.findViewById(R.id.addonCheckBox);
                    EditText nameEdit = addonView.findViewById(R.id.addonNameEdit);
                    EditText priceEdit = addonView.findViewById(R.id.addonPriceEdit);

                    String name = nameEdit.getText().toString().trim();
                    String price = priceEdit.getText().toString().trim();

                    if (!name.isEmpty() && !price.isEmpty()) {
                        Addon addon = new Addon(name, price, addonCheckBox.isChecked());
                        addons.add(addon);
                    }
                }
                item.setAddons(addons);
            } else {
                item.setHasAddons(false);
            }

            databaseReference.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(coffeeinv.this, "Coffee added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(coffeeinv.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Do nothing
    }
}