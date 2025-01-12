package com.example.ondwings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Queue extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    ArrayList<QueueItem> items;
    ImageView drawern;
    DatabaseReference databaseReference;
    QueueAdapter queueAdapter;
    DrawerLayout drawerLayout;
    TextView textView;
    int screenWidth;
    static final int ANIMATION_DURATION = 10000;
    private static final int ANIMATION_DELAY = 10;
    Handler handler;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.queue);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.queueRecyclerView);
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");
        items = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        queueAdapter = new QueueAdapter(this, items);
        recyclerView.setAdapter(queueAdapter);
        textView = findViewById(R.id.inprocess);

        drawern = findViewById(R.id.drawerp);
        drawerLayout = findViewById(R.id.drawer_layout); // Initialize DrawerLayout

        // Get the screen width
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        handler = new Handler();
        // Start moving animation
        moveText();

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
                    QueueItem queueItem = dataSnapshot.getValue(QueueItem.class);
                    items.add(queueItem);
                }
                queueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

    }

    private void moveText() {
        // Create a TranslateAnimation to move the text from left to right
        TranslateAnimation animation = new TranslateAnimation(-screenWidth, screenWidth, 0, 0);
        animation.setDuration(ANIMATION_DURATION); // Duration in milliseconds
        animation.setFillAfter(true); // Maintain the final position after animation ends
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Delay before starting the animation again
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveText(); // Start the animation again
                    }
                }, ANIMATION_DELAY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        textView.startAnimation(animation);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle Home click
            Intent intent = new Intent(Queue.this, Dashboard.class);
            startActivity(intent);
        } else if (id == R.id.sales) {
            // Handle Sales History click
            Intent intent = new Intent(Queue.this, Sales.class);
            startActivity(intent);
        } else if (id == R.id.menu) {
            // Handle Sales History click
            Intent intent = new Intent(Queue.this,coffeeee.class);
            startActivity(intent);
        } else if (id == R.id.invent) {
            // Handle Inventory click
            Intent intent = new Intent(Queue.this, coffeeprd.class);
            startActivity(intent);
        } else if (id == R.id.queue) {
            // Handle Inventory click
            Intent intent = new Intent(Queue.this,Queue.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            // Handle Inventory click
            Intent intent = new Intent(Queue.this, About.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog before logging out
            AlertDialog.Builder builder = new AlertDialog.Builder(Queue.this);
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to logout?");

            // Add positive button for confirmation
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Sign out from Firebase
                    mAuth.signOut();
                    
                    // Navigate to login screen and clear activity stack
                    Intent intent = new Intent(Queue.this, loginJ.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(Queue.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ;
        }
    }
}
