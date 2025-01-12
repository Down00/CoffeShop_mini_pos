package com.example.ondwings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Sales extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ImageView drawern;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        CardView saleshistory = findViewById(R.id.saleshistory);
        CardView dailysales = findViewById(R.id.dailysales);
        CardView weeklysales = findViewById(R.id.weeklysales);
        CardView monthlysales = findViewById(R.id.monthlysales);
        CardView yearlysales = findViewById(R.id.yearlysales);
        drawern = findViewById(R.id.drawerd);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout

        // Set up navigation item click listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        saleshistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sales.this, saleshistorycoffee.class);
                startActivity(intent);
            }
        });
        dailysales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sales.this, dailysales.class);
                startActivity(intent);
            }
        });
        weeklysales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sales.this, weeklysales.class);
                startActivity(intent);
            }
        });
        monthlysales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sales.this, monthlysales.class);
                startActivity(intent);
            }
        });
        yearlysales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Sales.this, yearlysales.class);
                startActivity(intent);
            }
        });
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
            Intent intent = new Intent(Sales.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(Sales.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(Sales.this, coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(Sales.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(Sales.this, Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(Sales.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(Sales.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Sign out from Firebase
                    mAuth.signOut();
                    
                    // Navigate to login screen and clear activity stack
                    Intent intent = new Intent(Sales.this, loginJ.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(Sales.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                    finish();
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
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Do nothing
    }
}


