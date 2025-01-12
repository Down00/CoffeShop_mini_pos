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
import java.util.HashMap;
import java.util.List;

public class saleshistorycoffeeAdapter extends RecyclerView.Adapter<saleshistorycoffeeAdapter.myViewHolder> {

    Context context;
    List<SalesHistoryItem2> itemList;
    HashMap<String, Integer> itemCounts;

    public saleshistorycoffeeAdapter(List<SalesHistoryItem2> itemList) {this.itemList = itemList;}

    public void setFilteredList (List<SalesHistoryItem2> filteredList){
        this.itemList= filteredList;
        notifyDataSetChanged();
    }

    public saleshistorycoffeeAdapter(Context context, ArrayList<SalesHistoryItem2> list) {
        this.context = context;
        this.itemList = list;
        this.itemCounts = new HashMap<>();
        calculateItemCounts();
    }

    private void calculateItemCounts() {
        for (SalesHistoryItem2 item : itemList) {
            String productName = item.getProductName();
            itemCounts.put(productName, itemCounts.getOrDefault(productName, 0) + 1);
        }
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.saleshistory2, parent, false);
        return new myViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        SalesHistoryItem2 salesHistoryItem2 = itemList.get(position);

        // Check if salesHistoryItem is not null to avoid NullPointerException
        if (salesHistoryItem2 != null) {
            holder.productcode.setText(salesHistoryItem2.getRandom());
            holder.datee.setText(salesHistoryItem2.getDate());
            holder.time.setText(salesHistoryItem2.getTime());
            holder.menuname.setText(salesHistoryItem2.getProductName());

            // Set OnClickListener for the "View Full Information" button
            holder.btnview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display the full product information dialog
                    displayProductInformation(salesHistoryItem2);
                }
            });
        }
        // Set OnClickListener for the "Delete" button
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog(salesHistoryItem2); // Show confirmation dialog before deleting
            }
        });
    }

    // Function to show confirmation dialog
    private void showConfirmationDialog(SalesHistoryItem2 salesHistoryItem2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure?");
        builder.setMessage("Deleted Data can't be Undo.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(salesHistoryItem2); // Delete the item if user confirms
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
    private void deleteItem(SalesHistoryItem2 salesHistoryItem2) {
        int position = itemList.indexOf(salesHistoryItem2);
        if (position != -1) {
            itemList.remove(position); // Remove item from the list
            notifyItemRemoved(position); // Notify adapter about the item removal
            deleteDataFromDatabase(salesHistoryItem2); // Delete item from the database
        }
    }

    // Function to delete data from the database
    private void deleteDataFromDatabase(SalesHistoryItem2 salesHistoryItem2) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SalesHistory");
        // Query the database to find the specific item to delete
        Query query = databaseReference.orderByChild("productName").equalTo(salesHistoryItem2.getProductName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QueueItem item = snapshot.getValue(QueueItem.class);
                    if (item != null && item.getTime().equals(salesHistoryItem2.getTime())) {
                        snapshot.getRef().removeValue(); // Remove the specific item from the database
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void displayProductInformation(SalesHistoryItem2 salesHistoryItem2) {
        double total = calculateTotal(salesHistoryItem2.getQuantity(), salesHistoryItem2.getPrice());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Menu Information");
        builder.setMessage("Category: " + salesHistoryItem2.getProductCode() + "\n" +
                "Name: " + salesHistoryItem2.getProductName() + "\n" +
                "Size: " + salesHistoryItem2.getSize() + "\n" +
                "AddOns: " + salesHistoryItem2.getAddOns() + "\n" +
                "Quantity: " + salesHistoryItem2.getQuantity() + "\n" +
                "Price: ₱" + salesHistoryItem2.getPrice() + "\n" +
                "Total: ₱" + salesHistoryItem2.getTotal());

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }

    private double calculateTotal(String quantity, String price) {
        try {
            int qty = Integer.parseInt(quantity);
            double unitPrice = Double.parseDouble(price);
            return qty * unitPrice;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Handle this case according to your logic
        }
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }

    private void performDataDeletion() {
        // Delete all data from the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("SalesHistory");
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
        TextView productcode, datee, time, menuname;
        Button btnview ,btndelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            productcode = itemView.findViewById(R.id.pproductcode);
            datee = itemView.findViewById(R.id.ddate);
            menuname = itemView.findViewById(R.id.menuname);
            btnview = itemView.findViewById(R.id.bbtnview);
            time = itemView.findViewById(R.id.ttime);
            btndelete = itemView.findViewById(R.id.bbtndeleted);
        }
    }
}