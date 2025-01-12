package com.example.ondwings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class coffeeAdapter extends RecyclerView.Adapter<coffeeAdapter.myViewHolder> {

    Context context;
    List<coffeeItem> itemList;

    public coffeeAdapter(List<coffeeItem> itemList) {
        this.itemList = itemList;
    }

    public void setFilteredList(List<coffeeItem> filteredList) {
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    public coffeeAdapter(Context context, ArrayList<coffeeItem> list) {
        this.context = context;
        this.itemList = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.coffeeinventory, parent, false);
        return new myViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        coffeeItem coffeeItem = itemList.get(position);
        holder.productcode.setText(coffeeItem.getProductcode());
        holder.productname.setText(coffeeItem.getProductname());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the product category is "THE BBC SIGNATURE" or "MONSTER COMBO"
                if (coffeeItem.getProductcode().equals("THE BBC SIGNATURE") || coffeeItem.getProductcode().equals("MONSTER COMBO")) {
                    // Show the dialog for editing name and price only
                    DialogPlus editDialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                            .setContentHolder(new ViewHolder(R.layout.update_popup))
                            .setGravity(Gravity.CENTER) // Center the dialog on the screen
                            .setExpanded(true, 1100) // Adjust height as needed
                            .setContentBackgroundResource(R.drawable.rounded_corners) // Set the rounded corners background
                            .create();

                    View editView = editDialogPlus.getHolderView();
                    EditText coffeename = editView.findViewById(R.id.txtproductname);
                    EditText price = editView.findViewById(R.id.txtprice); // Assuming you're using the tall price EditText for both tall and grande prices
                    Button btnUpdate = editView.findViewById(R.id.btnupdate);

                    coffeename.setText(coffeeItem.getProductname());
                    price.setText(String.valueOf(coffeeItem.getPrice())); // Convert Double to String

                    editDialogPlus.show();

                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the updated name and price
                            String updatedName = coffeename.getText().toString().trim();
                            String updatedPriceString = price.getText().toString().trim();

                            // Check if any of the fields are empty
                            if (updatedName.isEmpty() || updatedPriceString.isEmpty()) {
                                Toast.makeText(holder.itemView.getContext(), "Please enter both name and price", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Update the coffee item with the new name and price
                            coffeeItem.setProductname(updatedName);
                            coffeeItem.setPrice(updatedPriceString);

                            // Implement update logic here
                            // For example, if you want to update the item in the database:
                            DatabaseReference coffeeRef = FirebaseDatabase.getInstance().getReference().child("Menu").child(coffeeItem.getId());
                            coffeeRef.child("productname").setValue(updatedName);
                            coffeeRef.child("price").setValue(updatedPriceString);

                            // Dismiss the dialog after updating
                            editDialogPlus.dismiss();
                        }
                    });
                } else {
                    // Show the main dialog for editing all details
                    final DialogPlus dialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                            .setContentHolder(new ViewHolder(R.layout.update_popup2)) // Use the main layout for editing all details
                            .setGravity(Gravity.CENTER) // Center the dialog on the screen
                            .setExpanded(true, 1250)
                            .setContentBackgroundResource(R.drawable.rounded_corners) // Set the rounded corners background
                            .create();

                    View view = dialogPlus.getHolderView();
                    EditText coffeename = view.findViewById(R.id.txtproductname);
                    EditText pricetall = view.findViewById(R.id.txtpricetall);
                    EditText pricegrande = view.findViewById(R.id.txtpricegrande);

                    Button btnUpdate = view.findViewById(R.id.btnupdate);

                    coffeename.setText(coffeeItem.getProductname());
                    pricetall.setText(String.valueOf(coffeeItem.getPricetall())); // Convert Double to String
                    pricegrande.setText(String.valueOf(coffeeItem.getPricegrande()));

                    dialogPlus.show();

                    btnUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the updated name and prices
                            String updatedName = coffeename.getText().toString().trim();
                            String updatedPriceTall = pricetall.getText().toString().trim();
                            String updatedPriceGrande = pricegrande.getText().toString().trim();

                            // Check if any of the fields are empty
                            if (updatedName.isEmpty() || updatedPriceTall.isEmpty() || updatedPriceGrande.isEmpty()) {
                                Toast.makeText(holder.itemView.getContext(), "Please enter name, tall price, and grande price", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Update the coffee item with the new name and prices
                            coffeeItem.setProductname(updatedName);
                            coffeeItem.setPricetall(String.valueOf(updatedPriceTall));
                            coffeeItem.setPricegrande(String.valueOf(updatedPriceGrande));

                            // Implement update logic here
                            // For example, if you want to update the item in the database:
                            DatabaseReference coffeeRef = FirebaseDatabase.getInstance().getReference().child("Menu").child(coffeeItem.getId());
                            coffeeRef.child("productname").setValue(updatedName);
                            coffeeRef.child("pricetall").setValue(updatedPriceTall);
                            coffeeRef.child("pricegrande").setValue(updatedPriceGrande);
                            // Data updated successfully
                            Toast.makeText(holder.itemView.getContext(), "Data Updated Successfully", Toast.LENGTH_SHORT).show();
                            dialogPlus.dismiss();
                        }
                    });
                }
            }
        });

        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Are you sure?");
                builder.setMessage("Deleted Data can't be Undo.");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String itemId = coffeeItem.getId(); // Get the key of the item
                        FirebaseDatabase.getInstance().getReference().child("Menu")
                                .child(itemId).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.itemView.getContext(), "Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.itemView.getContext(), "Error While Deleting Data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }


        @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView productcode, productname;
        Button btnEdit, btndelete;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            productcode = itemView.findViewById(R.id.productcodee);
            productname = itemView.findViewById(R.id.productnamee);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btndelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

