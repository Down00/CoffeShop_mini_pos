package com.example.ondwings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{

    public CardView  queue, dailysales, inv, contact;
    public Button out;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        out = (Button) findViewById(R.id.logout);
        queue = (CardView) findViewById(R.id.queue);
        inv = (CardView) findViewById(R.id.inv);
        dailysales = (CardView) findViewById(R.id.btndailysales);
        contact = (CardView) findViewById(R.id.contact);

        queue.setOnClickListener((View.OnClickListener) this);
        inv.setOnClickListener((View.OnClickListener) this);
        contact.setOnClickListener((View.OnClickListener) this);
        dailysales.setOnClickListener((View.OnClickListener) this);

        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");

                // Set up the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Sign out from Firebase
                        mAuth.signOut();
                        
                        // Navigate to login screen
                        Intent intent = new Intent(Dashboard.this, loginJ.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        Toast.makeText(Dashboard.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked No button, dismiss the dialog
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
            }
        });
    }
        @Override
    public void onClick(View v) {
        Intent i;
        int id = v.getId();
        if (id == R.id.queue) {
            i = new Intent(this, Queue.class);
        } else if (id == R.id.inv) {
            i = new Intent(this, coffeeprd.class);
        } else if (id == R.id.contact) {
            i = new Intent(this, coffeeee.class);
        } else if (id == R.id.btndailysales) {
            i = new Intent(this, Sales.class);
        } else {// Handle other cases if needed
            return;
        }
        startActivity(i);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Do nothing
    }
}