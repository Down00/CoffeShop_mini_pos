package com.example.ondwings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class coffeeee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<coffeeItem> items;
    ImageView drawern, filterlist;
    DatabaseReference databaseReference;
    fcAdaptor fcAdaptor;
    DrawerLayout drawerLayout;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffeerecy);

        recyclerView = findViewById(R.id.recycle);
        databaseReference = FirebaseDatabase.getInstance().getReference("Menu");
        items = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fcAdaptor= new fcAdaptor(this, items);
        recyclerView.setAdapter(fcAdaptor);
        searchView = findViewById(R.id.searchView);
        drawern = findViewById(R.id.drawerd);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout
        filterlist = findViewById(R.id.filterlist);
        searchView.clearFocus();

        // Add OnClickListener for filter list ImageView
        filterlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCategoryListDialog();
            }
        });

        // Setup toggle button for the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle click on navigation drawer button
        drawern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Set up navigation item click listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    coffeeItem coffeeItem = dataSnapshot.getValue(coffeeItem.class);
                    items.add(coffeeItem);
                }
                fcAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });
    }

    private void filterList(String text) {
        List<coffeeItem> filteredList = new ArrayList<>();
        for (coffeeItem coffeeItem : items) {
            if (coffeeItem != null && coffeeItem.getProductname() != null && text != null && coffeeItem.getProductname().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(coffeeItem);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            fcAdaptor.setFilteredList(filteredList);
        }
    }
    private void showCategoryListDialog() {
        final String[] categories = {"None", "ICED COFFEE", "HOT DRINK", "SPECIAL DRINK","FRUITEAS",  "NON-COFFEE ( HOT )","COFFEE ( HOT )", "MONSTER COMBO", "THE BBC SIGNATURE"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Category");
        builder.setItems(categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedCategory = categories[which];
                if (selectedCategory.equals("None")) {
                    // If "None" is selected, show all products
                    fcAdaptor.setFilteredList(items);
                } else {
                    // Filter products by the selected category
                    filterProductsByCategory(selectedCategory);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }

    private void filterProductsByCategory(String category) {
        List<coffeeItem> filteredList = new ArrayList<>();
        for (coffeeItem coffeeItem : items) {
            String itemCategory = coffeeItem.getProductcode();
            if (itemCategory != null && itemCategory.equalsIgnoreCase(category)) {
                filteredList.add(coffeeItem);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No products found in this category", Toast.LENGTH_SHORT).show();
        } else {
            fcAdaptor.setFilteredList(filteredList);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle Home click
            Intent intent = new Intent(coffeeee.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(coffeeee.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(coffeeee.this, coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(coffeeee.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(coffeeee.this, Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(coffeeee.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(coffeeee.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle Logout click
                    Intent intent = new Intent(coffeeee.this, loginJ.class);
                    startActivity(intent);
                    Toast.makeText(coffeeee.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                }
            });

            // Add negative button for cancellation
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
        }
    }
}


