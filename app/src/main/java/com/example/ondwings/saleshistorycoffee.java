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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class saleshistorycoffee extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<SalesHistoryItem2> items;
    ImageView drawern, filterlist, btndeleteall;
    DatabaseReference databaseReference;
    saleshistorycoffeeAdapter saleshistorycoffeeAdapter;
    DrawerLayout drawerLayout;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saleshistorycoffee);

        recyclerView = findViewById(R.id.SalesHistoryRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("SalesHistory");
        items = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        saleshistorycoffeeAdapter = new saleshistorycoffeeAdapter(this, items);
        recyclerView.setAdapter(saleshistorycoffeeAdapter);
        searchView = findViewById(R.id.searchView);

        drawern = findViewById(R.id.drawerp);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout
        searchView.clearFocus();

        filterlist = findViewById(R.id.filterlist);
        btndeleteall = findViewById(R.id.btndeletealll);

        // Call deleteAllData() method when needed, e.g., on button click
        btndeleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleshistorycoffeeAdapter.deleteAllData();
            }
        });
        filterlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterOptions();
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
                    SalesHistoryItem2 salesHistoryItem2 = dataSnapshot.getValue(SalesHistoryItem2.class);
                    items.add(salesHistoryItem2);
                }
                saleshistorycoffeeAdapter.notifyDataSetChanged();
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

    private void showFilterOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sales Options");
        String[] options = {"    None", "★Best Seller", "    Lowest Sales", "    Highest to Lowest Sales"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // None
                        saleshistorycoffeeAdapter.setFilteredList(items); // Show all items
                        break;
                    case 1: // Highest Sales
                        filterItemsByHighestSales();
                        break;
                    case 2: // Lowest Sales
                        filterItemsByLowestSales();
                        break;
                    case 3: // Highest to Lowest
                        showAllDataSortedBySales();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);;
    }

    private void filterItemsByHighestSales() {
        // Calculate the count of orders for each product name
        HashMap<String, Integer> productOrdersCount = new HashMap<>();
        for (SalesHistoryItem2 item : items) {
            String productName = item.getProductName();
            if (productOrdersCount.containsKey(productName)) {
                productOrdersCount.put(productName, productOrdersCount.get(productName) + 1);
            } else {
                productOrdersCount.put(productName, 1);
            }
        }

        // Find the maximum count of orders
        int maxCount = Collections.max(productOrdersCount.values());

        // Filter items to include only those with the maximum count of orders
        List<SalesHistoryItem2> filteredList = new ArrayList<>();
        for (SalesHistoryItem2 item : items) {
            if (productOrdersCount.get(item.getProductName()) == maxCount) {
                filteredList.add(item);
            }
        }

        // Update the RecyclerView with the filtered list
        saleshistorycoffeeAdapter.setFilteredList(filteredList);
    }

    private void filterItemsByLowestSales() {
        // Calculate the count of orders for each product name
        HashMap<String, Integer> productOrdersCount = new HashMap<>();
        for (SalesHistoryItem2 item : items) {
            String productName = item.getProductName();
            if (productOrdersCount.containsKey(productName)) {
                productOrdersCount.put(productName, productOrdersCount.get(productName) + 1);
            } else {
                productOrdersCount.put(productName, 1);
            }
        }

        // Find the minimum count of orders
        int minCount = Collections.min(productOrdersCount.values());

        // Filter items to include only those with the minimum count of orders
        List<SalesHistoryItem2> filteredList = new ArrayList<>();
        for (SalesHistoryItem2 item : items) {
            if (productOrdersCount.get(item.getProductName()) == minCount) {
                filteredList.add(item);
            }
        }

        // Update the RecyclerView with the filtered list
        saleshistorycoffeeAdapter.setFilteredList(filteredList);
    }

    private void showAllDataSortedBySales() {
        // Calculate the count of orders for each product name
        HashMap<String, Integer> productOrdersCount = new HashMap<>();
        for (SalesHistoryItem2 item : items) {
            String productName = item.getProductName();
            if (productOrdersCount.containsKey(productName)) {
                productOrdersCount.put(productName, productOrdersCount.get(productName) + 1);
            } else {
                productOrdersCount.put(productName, 1);
            }
        }

        // Sort the product names based on the count of orders (from highest to lowest)
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productOrdersCount.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Create a list to hold the sorted items
        List<SalesHistoryItem2> sortedList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String productName = entry.getKey();
            for (SalesHistoryItem2 item : items) {
                if (item.getProductName().equals(productName)) {
                    sortedList.add(item);
                }
            }
        }

        // Update the RecyclerView with the sorted list
        saleshistorycoffeeAdapter.setFilteredList(sortedList);
    }

    private void filterList(String text) {
        List<SalesHistoryItem2> filteredList = new ArrayList<>();
        for (SalesHistoryItem2 salesHistoryItem2 : items) {
            if (salesHistoryItem2.getRandom().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(salesHistoryItem2);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            saleshistorycoffeeAdapter.setFilteredList(filteredList);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle Home click
            Intent intent = new Intent(saleshistorycoffee.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(saleshistorycoffee.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(saleshistorycoffee.this, coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(saleshistorycoffee.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(saleshistorycoffee.this, Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(saleshistorycoffee.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(saleshistorycoffee.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle Logout click
                    Intent intent = new Intent(saleshistorycoffee.this, loginJ.class);
                    startActivity(intent);
                    Toast.makeText(saleshistorycoffee.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
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
