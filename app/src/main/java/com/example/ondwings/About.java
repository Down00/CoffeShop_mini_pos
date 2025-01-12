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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class About extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView drawern;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        drawern = findViewById(R.id.drawerd);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle Home click
            Intent intent = new Intent(About.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(About.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(About.this, coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(About.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(About.this, Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(About.this,About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Handle Logout click
                    Intent intent = new Intent(About.this, loginJ.class);
                    startActivity(intent);
                    Toast.makeText(About.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
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


