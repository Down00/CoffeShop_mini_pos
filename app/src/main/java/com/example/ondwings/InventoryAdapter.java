package com.example.ondwings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.myViewHolder> {

    Context context;
    public InventoryAdapter(List<Item> itemList) {this.itemList = itemList;}
    List<Item> itemList;
    private Item selectedItem; // Add a variable to track selected item
    public void setFilteredList (List<Item> filteredList){
        this.itemList= filteredList;
        notifyDataSetChanged();
    }

    public InventoryAdapter(Context context, ArrayList<Item> list) {
        this.context = context;
        this.itemList = list;
        /*this.filteredList = new ArrayList<>(list); // Initialize filteredList with the original list*/
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.productcode.setText(item.getProductcode());
        holder.productname.setText(item.getProductname());
        
        // Create a formatted price string
        StringBuilder priceText = new StringBuilder();
        if (item.getPricetall() != null && item.getPricegrande() != null) {
            priceText.append("Tall: ₱").append(item.getPricetall())
                    .append("\nGrande: ₱").append(item.getPricegrande());
        } else if (item.getPrice() != null && !item.getPrice().isEmpty()) {
            priceText.append("Price: ₱").append(item.getPrice());
        }
        holder.price.setText(priceText.toString());

        // Show add-ons if available
        if (item.isHasAddons() && item.getAddons() != null && !item.getAddons().isEmpty()) {
            StringBuilder addonsText = new StringBuilder("Add-ons:\n");
            for (Addon addon : item.getAddons()) {
                if (addon.isEnabled()) {
                    addonsText.append("• ").append(addon.getName())
                            .append(" (₱").append(addon.getPrice()).append(")\n");
                }
            }
            holder.addons.setVisibility(View.VISIBLE);
            holder.addons.setText(addonsText.toString());
        } else {
            holder.addons.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.productcode.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup2))
                        .setExpanded(true, 1200)
                        .create();

                View view = dialogPlus.getHolderView();
                EditText productname = view.findViewById(R.id.txtproductname);
                EditText price = view.findViewById(R.id.txtprice);
                EditText pricetall = view.findViewById(R.id.txtpricetall);
                EditText pricegrande = view.findViewById(R.id.txtpricegrande);
                Button btnUpdate = view.findViewById(R.id.btnupdate);

                // Set values based on price type
                productname.setText(item.getProductname());
                
                if (item.getPricetall() != null && item.getPricegrande() != null) {
                    pricetall.setText(item.getPricetall());
                    pricegrande.setText(item.getPricegrande());
                    pricetall.setVisibility(View.VISIBLE);
                    pricegrande.setVisibility(View.VISIBLE);
                    price.setVisibility(View.GONE);
                } else {
                    price.setText(item.getPrice());
                    price.setVisibility(View.VISIBLE);
                    pricetall.setVisibility(View.GONE);
                    pricegrande.setVisibility(View.GONE);
                }

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("productname", productname.getText().toString());
                        
                        // Update correct price fields
                        if (pricetall.getVisibility() == View.VISIBLE) {
                            map.put("pricetall", pricetall.getText().toString());
                            map.put("pricegrande", pricegrande.getText().toString());
                        } else {
                            map.put("price", price.getText().toString());
                        }

                        // Preserve add-ons
                        if (item.isHasAddons()) {
                            map.put("hasAddons", true);
                            map.put("addons", item.getAddons());
                        }

                        FirebaseDatabase.getInstance().getReference().child("Menu")
                                .child(item.getId()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(holder.productcode.getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(holder.productcode.getContext(), "Error while Updating", Toast.LENGTH_SHORT).show();
                                        dialogPlus.dismiss();
                                    }
                                });
                    }
                });
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.productcode.getContext());
                builder.setTitle("Are you sure you want to delete?");
                builder.setMessage("Deleted data can't be undone.");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference().child("Menu")
                                .child(item.getId()).removeValue();
                        Toast.makeText(holder.productcode.getContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(holder.productcode.getContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView productcode, productname, price, addons, quantity;

        Button btnEdit, btnDelete;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            productcode = itemView.findViewById(R.id.productcode);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            productname = itemView.findViewById(R.id.productname);
            addons = itemView.findViewById(R.id.addonsList);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}
