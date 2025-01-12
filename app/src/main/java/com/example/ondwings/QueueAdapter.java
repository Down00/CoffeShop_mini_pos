package com.example.ondwings;

import android.app.AlertDialog;
import android.content.Context;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.myViewHolder> {

    Context context;
    List<QueueItem> itemList;
    double dailyTotal; // Daily sales total
    double weeklyTotal;
    double monthlyTotal;
    double yearlyTotal;


    public QueueAdapter(Context context, ArrayList<QueueItem> list) {
        this.context = context;
        this.itemList = list;
        this.dailyTotal = 0.0; // Initialize daily total to 0
        this.weeklyTotal = 0.0;
        this.monthlyTotal = 0.0;
        this.yearlyTotal = 0.0;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.queuee, parent, false);
        return new myViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        QueueItem queueItem = itemList.get(position);

        // Check if queueItem is not null to avoid NullPointerException
        if (queueItem != null) {
            holder.customnumber.setText(queueItem.getRandomCode());
            holder.time.setText(queueItem.getTime());
            holder.datee.setText(queueItem.getDate());

            // Set OnClickListener for the "View Full Information" button
            holder.btnview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display the full product information dialog
                    displayProductInformation(queueItem);
                }
            });
        }

        // Set OnClickListener for the "Confirm" button
        holder.btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Push item to SaleHistory RecyclerView
                pushToSaleHistory(queueItem);

                // Update daily sales total
                updateDailySalesTotal(queueItem.getTotal());

                // Update weekly sales total
                updateWeeklySalesTotal(queueItem.getTotal());

                // Update monthly sales total
                updateMonthlySalesTotal(queueItem.getTotal());

                // Update yearly sales total
                updateYearlySalesTotal(queueItem.getTotal());

                // Remove the item from the queue
                deleteItem(queueItem);
            }
        });
    }

    // Push item to SaleHistory RecyclerView
    private void pushToSaleHistory(QueueItem queueItem) {
        // Create a SalesHistoryItem2 object
        SalesHistoryItem2 salesHistoryItem2 = new SalesHistoryItem2();

        // Set all fields in the correct order
        salesHistoryItem2.setProductCode(queueItem.getProductCode());  // Category
        salesHistoryItem2.setProductName(queueItem.getProductName());  // Product name
        salesHistoryItem2.setSize(queueItem.getSize());               // Size
        salesHistoryItem2.setQuantity(queueItem.getQuantity());       // Quantity
        salesHistoryItem2.setPrice(queueItem.getPrice());             // Price
        salesHistoryItem2.setTotal(queueItem.getTotal());             // Total
        salesHistoryItem2.setDate(queueItem.getDate());               // Date
        salesHistoryItem2.setTime(queueItem.getTime());               // Time
        salesHistoryItem2.setAddOns(queueItem.getAddOns());          // Add-ons
        salesHistoryItem2.setRandom(queueItem.getRandomCode());       // Customer code

        // Push salesHistoryItem to Firebase Realtime Database
        DatabaseReference saleHistoryRef = FirebaseDatabase.getInstance().getReference("SalesHistory");
        saleHistoryRef.push().setValue(salesHistoryItem2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Sale history item added successfully
                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add sale history item
                        Toast.makeText(context, "Failed to add item to Sale History", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Update daily sales total
    private void updateDailySalesTotal(double total) {

        // Get the current daily sales total
        DatabaseReference dailySalesRef = FirebaseDatabase.getInstance().getReference("DailySales");
        // Update Daily Sales
        dailySalesRef.child(getCurrentDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the record exists, update the total
                    double existingTotal = dataSnapshot.child("total").getValue(Double.class);
                    double newTotal = existingTotal + total;
                    dailySalesRef.child(getCurrentDate()).child("total").setValue(newTotal);
                } else {
                    // If the record doesn't exist, create a new one
                    dailyItem dailySalesItem = new dailyItem(getCurrentDate(), total);
                    dailySalesRef.child(getCurrentDate()).setValue(dailySalesItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateWeeklySalesTotal(double total) {
        // Get the current date
        String currentDate = getCurrentDate();

        // Get the current week of the month
        int currentWeek = getCurrentWeek();

        // Get the reference to the weekly sales node
        DatabaseReference weeklySalesRef = FirebaseDatabase.getInstance().getReference("WeeklySales");

        // Update Weekly Sales
        weeklySalesRef.child("Week" + currentWeek).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the record exists, update the total
                    double existingTotal = dataSnapshot.child("total").getValue(Double.class);
                    double newTotal = existingTotal + total;
                    weeklySalesRef.child("Week" + currentWeek).child("total").setValue(newTotal);
                } else {
                    // If the record doesn't exist, create a new one
                    weeklyItem weeklyItem = new weeklyItem(currentDate, total);
                    weeklySalesRef.child("Week" + currentWeek).setValue(weeklyItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateMonthlySalesTotal(double total) {
        // Get the current date
        String currentDate = getCurrentDate();

        // Get the current month
        int currentMonth = getCurrentMonth();

        // Get the reference to the monthly sales node
        DatabaseReference monthlySalesRef = FirebaseDatabase.getInstance().getReference("MonthlySales");

        // Update Monthly Sales
        monthlySalesRef.child("Month" + currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the record exists, update the total
                    double existingTotal = dataSnapshot.child("total").getValue(Double.class);
                    double newTotal = existingTotal + total;
                    monthlySalesRef.child("Month" + currentMonth).child("total").setValue(newTotal);
                } else {
                    // If the record doesn't exist, create a new one
                    monthlyItem monthlyItem = new monthlyItem(currentDate, total);
                    monthlySalesRef.child("Month" + currentMonth).setValue(monthlyItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void updateYearlySalesTotal(double total) {
        // Get the current date
        String currentDate = getCurrentDate();

        // Get the current year
        int currentYear = getCurrentYear();

        // Get the reference to the yearly sales node
        DatabaseReference yearlySalesRef = FirebaseDatabase.getInstance().getReference("YearlySales");

        // Update Yearly Sales
        yearlySalesRef.child("Year" + currentYear).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If the record exists, update the total
                    double existingTotal = dataSnapshot.child("total").getValue(Double.class);
                    double newTotal = existingTotal + total;
                    yearlySalesRef.child("Year" + currentYear).child("total").setValue(newTotal);
                } else {
                    // If the record doesn't exist, create a new one
                    yearlyItem yearlyItem = new yearlyItem(currentDate, total);
                    yearlySalesRef.child("Year" + currentYear).setValue(yearlyItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    // Remove the item from the queue
    private void deleteItem(QueueItem queueItem) {
        int position = itemList.indexOf(queueItem);
        if (position != -1) {
            itemList.remove(position); // Remove item from the list
            notifyItemRemoved(position); // Notify adapter about the item removal
            deleteDataFromDatabase(queueItem); // Delete item from the database
        }
    }

    // Function to delete data from the database
    private void deleteDataFromDatabase(QueueItem queueItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Queue");
        // Query the database to find the specific item to delete
        Query query = databaseReference.orderByChild("productName").equalTo(queueItem.getProductName());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QueueItem item = snapshot.getValue(QueueItem.class);
                    if (item != null && item.getTime().equals(queueItem.getTime())) {
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

    // Display the full product information dialog
    private void displayProductInformation(QueueItem queueItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order Information");

        // Check if the product category is MONSTER COMBO or THE BBC SIGNATURE
        if (queueItem.getProductCode().equals("MONSTER COMBO") || queueItem.getProductCode().equals("THE BBC SIGNATURE")) {
            builder.setMessage("Category: " + queueItem.getProductCode() + "\n" +
                    "Name: " + queueItem.getProductName()+ "\n" +
                    "AddOns: " + queueItem.getAddOns() +
                    "Quantity: " + queueItem.getQuantity() + "\n" +
                    "Price: ₱" + queueItem.getPrice() + "\n" +
                    "Total: ₱" + queueItem.getTotal());
        } else {
            // For other categories, show default message
            builder.setMessage("Category: " + queueItem.getProductCode() + "\n" +
                    "Name: " + queueItem.getProductName() + "\n" +
                    "Size: " + queueItem.getSize() + "\n" +
                    "AddOns: " + queueItem.getAddOns() +
                    "Quantity: " + queueItem.getQuantity() + "\n" +
                    "Price: ₱" + queueItem.getPrice() + "\n" +
                    "Total: ₱" +queueItem.getTotal());
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }


    private String getCurrentDate() {
        // Get current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    private String getCurrentTime() {
        // Get current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date currentTime = new Date();
        return timeFormat.format(currentTime);

    }
    private int getCurrentWeek() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int weekOfMonth = (dayOfMonth - 1) / 7 + 1; // Calculate the week of the month
        return weekOfMonth;
    }

    // Method to get the current month
    private int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1; // Add 1 because Calendar.MONTH starts from 0
    }

    // Method to get the current year
    private int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView customnumber, datee, time;
        Button btnview ,btnconfirm;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            customnumber = itemView.findViewById(R.id.customernumber);
            datee = itemView.findViewById(R.id.ddate);
            btnview = itemView.findViewById(R.id.bbtnview);
            time = itemView.findViewById(R.id.ttime);
            btnconfirm = itemView.findViewById(R.id.bbtnconfirm);
        }
    }
}
