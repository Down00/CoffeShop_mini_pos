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

public class menuinv extends AppCompatActivity {

    private Spinner categorySpinner;
    private EditText productnameEditText, priceEditText;
    private Button saveButton, backButton;
    private CheckBox addonsCheckBox;
    private LinearLayout addonsContainer, addonsListContainer;
    private Button addNewAddonButton;
    private List<View> addonViews = new ArrayList<>();

    private String[] categories = {"Select Category", "MONSTER COMBO", "THE BBC SIGNATURE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_menu_activity);

        // Initialize views
        categorySpinner = findViewById(R.id.categorySpinner);
        productnameEditText = findViewById(R.id.productnameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        addonsCheckBox = findViewById(R.id.addonsCheckBox);
        addonsContainer = findViewById(R.id.addonsContainer);
        addonsListContainer = findViewById(R.id.addonsListContainer);
        addNewAddonButton = findViewById(R.id.addNewAddonButton);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);

        // Set up spinner
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

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(menuinv.this, coffeeinv.class));
            finish();
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
        String price = priceEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (productname.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Menu");
        String id = databaseReference.push().getKey();

        if (id != null) {
            menuItem item = new menuItem();
            item.setId(id);
            item.setProductcode(category);
            item.setProductname(productname);
            item.setPrice(price);
            
            // Handle add-ons
            if (addonsCheckBox.isChecked() && !addonViews.isEmpty()) {
                item.setHasAddons(true);
                List<Addon> addons = new ArrayList<>();
                
                for (View addonView : addonViews) {
                    CheckBox addonCheckBox = addonView.findViewById(R.id.addonCheckBox);
                    EditText nameEdit = addonView.findViewById(R.id.addonNameEdit);
                    EditText priceEdit = addonView.findViewById(R.id.addonPriceEdit);

                    String name = nameEdit.getText().toString().trim();
                    String addonPrice = priceEdit.getText().toString().trim();

                    if (!name.isEmpty() && !addonPrice.isEmpty()) {
                        Addon addon = new Addon(name, addonPrice, addonCheckBox.isChecked());
                        addons.add(addon);
                    }
                }
                item.setAddons(addons);
            } else {
                item.setHasAddons(false);
            }

            databaseReference.child(id).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(menuinv.this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(menuinv.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        startActivity(new Intent(menuinv.this, coffeeinv.class));
        finish();
    }
}