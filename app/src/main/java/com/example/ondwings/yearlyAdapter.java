package com.example.ondwings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class yearlyAdapter extends RecyclerView.Adapter<yearlyAdapter .myViewHolder> {

    Context context;
    List<yearlyItem> itemList;

    public yearlyAdapter (List<yearlyItem> itemList) {
        this.itemList = itemList;
    }

    public void setFilteredList(List<yearlyItem> filteredList) {
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    public yearlyAdapter (Context context, ArrayList<yearlyItem> list) {
        this.context = context;
        this.itemList = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ysales, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        yearlyItem yearlyItem = itemList.get(position);
        holder.datee.setText(yearlyItem.getDate());
        holder.total.setText(String.valueOf(yearlyItem.getTotal()));

        // Set OnClickListener for the "Delete" button
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(yearlyItem); // Show confirmation dialog before deleting
            }
        });
    }

    // Function to show confirmation dialog
    private void showConfirmationDialog(yearlyItem yearlyItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure?");
        builder.setMessage("Deleted Data can't be Undo.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(yearlyItem); // Delete the item if user confirms
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Dismiss the dialog if user cancels
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    // Function to delete item from the list and database
    private void deleteItem(yearlyItem yearlyItem) {
        int position = itemList.indexOf(yearlyItem);
        if (position != -1) {
            itemList.remove(position); // Remove item from the list
            notifyItemRemoved(position); // Notify adapter about the item removal
            deleteDataFromDatabase(yearlyItem); // Delete item from the database
        }
    }
    // Function to delete data from the database
    private void deleteDataFromDatabase(yearlyItem yearlyItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("YearlySales");
        // Query the database to find the data to delete
        Query query = databaseReference.orderByChild("date").equalTo(yearlyItem.getDate());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue(); // Remove the data from the database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    public void deleteAllData() {
        // Create an AlertDialog for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to delete all data?");

        // Positive button for confirmation
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle deletion here
                performDataDeletion();
            }
        });

        // Negative button for cancellation
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

    private void performDataDeletion() {
        // Delete all data from the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("YearlySales");
        databaseRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data deletion successful
                        Toast.makeText(context, "All data deleted successfully", Toast.LENGTH_SHORT).show();
                        // Clear the itemList and refresh the RecyclerView
                        itemList.clear();
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Data deletion failed
                        Toast.makeText(context, "Failed to delete all data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView datee, total;
        Button btndelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            datee = itemView.findViewById(R.id.dateee);
            total = itemView.findViewById(R.id.totall);
            btndelete = itemView.findViewById(R.id.btndelete);

        }
    }
}
