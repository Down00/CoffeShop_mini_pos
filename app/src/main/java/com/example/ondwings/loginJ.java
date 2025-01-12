package com.example.ondwings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginJ extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, redirect to Dashboard
            startActivity(new Intent(loginJ.this, Dashboard.class));
            finish(); // This prevents going back to login screen when pressing back
            return;
        }
        
        setContentView(R.layout.login);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        Button loginbtn = (Button) findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameValue = username.getText().toString();
                String passwordValue = password.getText().toString();

                if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
                    // Show dialog message if either username or password is empty
                    AlertDialog.Builder builder = new AlertDialog.Builder(loginJ.this);
                    builder.setTitle("Incomplete Information")
                            .setMessage("Please enter both username and password.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
                } else {
                    // Attempt to sign in with Firebase Auth
                    mAuth.signInWithEmailAndPassword(usernameValue, passwordValue)
                            .addOnCompleteListener(loginJ.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success
                                        Toast.makeText(loginJ.this, "Welcome to THE BBC Reserve!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(loginJ.this, Dashboard.class);
                                        startActivity(intent);
                                        finish(); // This prevents going back to login screen when pressing back
                                    } else {
                                        // If sign in fails, display a message to the user
                                        AlertDialog.Builder builder = new AlertDialog.Builder(loginJ.this);
                                        builder.setTitle("Authentication Failed")
                                                .setMessage("Invalid email or password.")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        AlertDialog dialog = builder.create();
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.show();
                                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
                                    }
                                }
                            });
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("Are you sure you want to exit the application?");

        // Set up the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Navigate to the Home screen
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No button, dismiss the dialog
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }
}
