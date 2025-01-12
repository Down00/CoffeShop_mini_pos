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

public class dailysales extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    ArrayList<dailyItem> list;
    ImageView drawern; // drawern is the navigation drawer button
    DatabaseReference databaseReference;
    dailyAdapter dailyAdapter;
    SearchView searchView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    ImageView btndeleteall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailysales);

        recyclerView = findViewById(R.id.recycle);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dailyAdapter = new dailyAdapter(this, list);
        recyclerView.setAdapter(dailyAdapter);
        drawern = findViewById(R.id.drawerd);
        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout
        databaseReference = FirebaseDatabase.getInstance().getReference("DailySales");

        btndeleteall =findViewById(R.id.btndeleteall);

        // Setup toggle button for the navigation drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        // Handle click on navigation drawer button
        drawern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btndeleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyAdapter.deleteAllData();
            }
        });

        // Set up navigation item click listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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


        // Add a ValueEventListener to retrieve data from the Firebase Realtime Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dailyItem dailyItem = dataSnapshot.getValue(dailyItem.class);
                    list.add(dailyItem);
                }
                dailyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void filterList(String text) {
        List<dailyItem> filteredList = new ArrayList<>();
        for (dailyItem dailyItem : list){
            if (dailyItem.getDate().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(dailyItem);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }else{
            dailyAdapter.setFilteredList(filteredList);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle Home click
            Intent intent = new Intent(dailysales.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(dailysales.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(dailysales.this, coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(dailysales.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(dailysales.this, Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(dailysales.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(dailysales.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle Logout click
                    Intent intent = new Intent(dailysales.this, loginJ.class);
                    startActivity(intent);
                    Toast.makeText(dailysales.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
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
