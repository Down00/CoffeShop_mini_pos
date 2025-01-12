package com.example.ondwings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class fcAdaptor extends RecyclerView.Adapter<fcAdaptor.myViewHolder> {

    Context context;
    List<coffeeItem> itemList;

    public fcAdaptor(List<coffeeItem> itemList) {
        this.itemList = itemList;
    }

    public void setFilteredList(List<coffeeItem> filteredList) {
        this.itemList = filteredList;
        notifyDataSetChanged();
    }

    public fcAdaptor(Context context, ArrayList<coffeeItem> list) {
        this.context = context;
        this.itemList = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.coffeemenue, parent, false);
        return new myViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        coffeeItem coffeeItem = itemList.get(position);
        holder.productcode.setText(coffeeItem.getProductcode());
        holder.productname.setText(coffeeItem.getProductname());

        holder.btnget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSizeSelectionDialog(coffeeItem, holder);
            }
        });
    }

    private void showSizeSelectionDialog(coffeeItem coffeeItem, myViewHolder holder) {
        String[] selectableCategories = {"ICED COFFEE", "HOT DRINK", "SPECIAL DRINK", "FRUITEAS", "NON-COFFEE ( HOT )", "COFFEE ( HOT )", "MONSTER COMBO", "THE BBC SIGNATURE"};

        // Check if the product category is one of the selectable categories
        if (Arrays.asList(selectableCategories).contains(coffeeItem.getProductcode())) {
            // For categories that require size selection
            if (!coffeeItem.getProductcode().equals("THE BBC SIGNATURE") && !coffeeItem.getProductcode().equals("MONSTER COMBO")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Select Size");

                // Set up the choices
                String[] sizes = {"Tall", "Grande"};
                builder.setItems(sizes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedSize = sizes[which];
                        double price = Double.parseDouble(selectedSize.equals("Tall") ? coffeeItem.getPricetall() : coffeeItem.getPricegrande());
                        if (price == 0) {
                            showNotAvailableDialog(selectedSize);
                        } else {
                            showEnterQuantityDialog(coffeeItem, holder, selectedSize);
                        }
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
            } else {
                // For BBC SIGNATURE and MONSTER COMBO, directly show quantity dialog
                showEnterQuantityDialog(coffeeItem, holder, "");
            }
        } else {
            Toast.makeText(context, "Invalid category selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotAvailableDialog(String selectedSize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Not Available")
                .setMessage("Sorry, the selected size (" + selectedSize + ") is not available at this moment.")
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


    private void showEnterQuantityDialog(coffeeItem coffeeItem, myViewHolder holder, String selectedSize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter Quantity");

        // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String quantityString = input.getText().toString();

                // Check if the quantity string is empty
                if (quantityString.isEmpty()) {
                    Toast.makeText(context, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                int enteredQuantity = Integer.parseInt(quantityString);
                double basePrice = selectedSize.isEmpty() ? 
                    Double.parseDouble(coffeeItem.getPrice()) :
                    Double.parseDouble(selectedSize.equals("Tall") ? 
                        coffeeItem.getPricetall() : coffeeItem.getPricegrande());
                double total = basePrice * enteredQuantity;

                // Generate customer code
                String customerCode = generateCustomerCode();

                // First show the menu information
                AlertDialog.Builder infoBuilder = new AlertDialog.Builder(context);
                infoBuilder.setTitle("Order Information");
                
                StringBuilder info = new StringBuilder();
                info.append("Product Name: ").append(coffeeItem.getProductname()).append("\n");
                info.append("Category: ").append(coffeeItem.getProductcode()).append("\n");
                info.append("Customer Code: ").append(customerCode).append("\n");
                if (!selectedSize.isEmpty()) {
                    info.append("Size: ").append(selectedSize).append("\n");
                    info.append("Price: ₱").append(selectedSize.equals("Tall") ? 
                        coffeeItem.getPricetall() : coffeeItem.getPricegrande()).append("\n");
                } else {
                    info.append("Price: ₱").append(coffeeItem.getPrice()).append("\n");
                }
                info.append("Quantity: ").append(enteredQuantity).append("\n");
                info.append("Total: ₱").append(String.format("%.2f", total));

                // Create custom view for dialog
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(50, 30, 50, 1);

                TextView messageView = new TextView(context);
                messageView.setText(info.toString());
                messageView.setTextSize(16);
                messageView.setLineSpacing(0, 1.2f);
                layout.addView(messageView);
                infoBuilder.setView(layout);
                if (coffeeItem.isHasAddons()) {
                    infoBuilder.setPositiveButton("Add Add-ons", (dialogInterface, id) -> 
                        showAddOnsDialog(coffeeItem, holder, enteredQuantity, selectedSize));
                    
                    infoBuilder.setNegativeButton("Confirm Without Add-ons", (dialogInterface, id) -> 
                        displayProductInformation(coffeeItem, holder, enteredQuantity, total, selectedSize, null));
                } else {
                    infoBuilder.setPositiveButton("Confirm", (dialogInterface, id) -> 
                        displayProductInformation(coffeeItem, holder, enteredQuantity, total, selectedSize, null));
                }

                infoBuilder.setNeutralButton("Cancel", (dialogInterface, id) -> dialogInterface.dismiss());

                AlertDialog infoDialog = infoBuilder.create();
                infoDialog.setCanceledOnTouchOutside(false);
                infoDialog.show();
                infoDialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }

    private String generateCustomerCode() {
        // Generate a random 3-character code using letters and numbers
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return "CUSTOMER " + code.toString();
    }

    private void showAddOnsDialog(coffeeItem coffeeItem, myViewHolder holder, int quantity, String selectedSize) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Add-ons");

        List<Addon> availableAddons = coffeeItem.getAddons();
        if (availableAddons == null || availableAddons.isEmpty()) {
            Toast.makeText(context, "No add-ons available", Toast.LENGTH_SHORT).show();
            // Calculate total without add-ons
            double basePrice = selectedSize.isEmpty() ? 
                Double.parseDouble(coffeeItem.getPrice()) :
                Double.parseDouble(selectedSize.equals("Tall") ? 
                    coffeeItem.getPricetall() : coffeeItem.getPricegrande());
            double total = basePrice * quantity;
            displayProductInformation(coffeeItem, holder, quantity, total, selectedSize, null);
            return;
        }

        // Create arrays for dialog
        String[] addOnArray = new String[availableAddons.size()];
        boolean[] selectedAddOns = new boolean[availableAddons.size()];
        
        // Populate arrays
        for (int i = 0; i < availableAddons.size(); i++) {
            Addon addon = availableAddons.get(i);
            addOnArray[i] = addon.getName() + " (₱" + addon.getPrice() + ")";
            selectedAddOns[i] = false;
        }

        builder.setMultiChoiceItems(addOnArray, selectedAddOns, 
            new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    selectedAddOns[which] = isChecked;
                }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Calculate base price
                double basePrice = selectedSize.isEmpty() ? 
                    Double.parseDouble(coffeeItem.getPrice()) :
                    Double.parseDouble(selectedSize.equals("Tall") ? 
                        coffeeItem.getPricetall() : coffeeItem.getPricegrande());
                double total = basePrice * quantity;

                // Add selected add-ons prices
                StringBuilder addOnsSummary = new StringBuilder();
                for (int i = 0; i < addOnArray.length; i++) {
                    if (selectedAddOns[i]) {
                        Addon addon = availableAddons.get(i);
                        total += Double.parseDouble(addon.getPrice()) * quantity;
                        addOnsSummary.append(addon.getName())
                                   .append(" (₱")
                                   .append(addon.getPrice())
                                   .append(")\n");
                    }
                }

                displayProductInformation(coffeeItem, holder, quantity, total, selectedSize, 
                    addOnsSummary.length() > 0 ? addOnsSummary.toString() : null);
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
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
    }

    private void displayProductInformation(coffeeItem coffeeItem, myViewHolder holder, int enteredQuantity, 
            double total, String selectedSize, String addOns) {
        // Generate customer code
        String customerCode = generateCustomerCode();

        // Create separate date and time formats
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date now = new Date();
        String currentDate = dateFormat.format(now);
        String currentTime = timeFormat.format(now);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders");
        String orderId = ordersRef.push().getKey();

        if (orderId != null) {
            StringBuilder orderDetails = new StringBuilder();
            orderDetails.append("Product: ").append(coffeeItem.getProductname()).append("\n");
            orderDetails.append("Category: ").append(coffeeItem.getProductcode()).append("\n");
            String price;
            if (!selectedSize.isEmpty()) {
                orderDetails.append("Size: ").append(selectedSize).append("\n");
                price = selectedSize.equals("Tall") ? coffeeItem.getPricetall() : coffeeItem.getPricegrande();
                orderDetails.append("Price: ₱").append(price).append("\n");
            } else {
                price = coffeeItem.getPrice();
                orderDetails.append("Price: ₱").append(price).append("\n");
            }
            orderDetails.append("Quantity: ").append(enteredQuantity).append("\n");
            
            // Calculate total
            double basePrice = Double.parseDouble(price);
            double itemTotal = basePrice * enteredQuantity;
            double addonTotal = 0.0;
            
            if (addOns != null && !addOns.isEmpty()) {
                orderDetails.append("Add-ons: ").append(addOns).append("\n");
                // Extract add-on prices and add to total (only once, not per item)
                String[] addonParts = addOns.split("\\(₱|\\)");
                for (int i = 1; i < addonParts.length; i += 2) {
                    try {
                        double addonPrice = Double.parseDouble(addonParts[i].trim());
                        addonTotal += addonPrice; // Add addon price only once
                    } catch (NumberFormatException e) {
                        //Log.e("fcAdaptor", "Error parsing addon price: " + e.getMessage());
                    }
                }
            }
            
            double finalTotal = itemTotal + addonTotal; // Add base total and addon total
            
            orderDetails.append("Total: ₱").append(String.format("%.2f", finalTotal)).append("\n");
            orderDetails.append("Date: ").append(currentDate).append("\n");
            orderDetails.append("Time: ").append(currentTime).append("\n");
            orderDetails.append("Customer Code: ").append(customerCode);

            // Create a QueueItem object
            QueueItem queueItem = new QueueItem(
                coffeeItem.getProductcode(),  // productCode
                coffeeItem.getProductname(),  // productName
                selectedSize,                 // size
                String.valueOf(enteredQuantity), // quantity
                price,                        // price
                finalTotal,                   // total as double
                currentDate,                  // date
                currentTime,                  // time
                customerCode,                 // randomCode
                addOns                        // addOns
            );

            // Save to Firebase
            ordersRef.child(orderId).setValue(queueItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Order placed successfully!\n\n" + orderDetails.toString(), 
                        Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to place order: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
        }
    }

    private double calculateTotal(String quantity, String price) {
        // Check if quantity or price is null
        if (quantity == null || price == null) {
            return 0.0; // Return 0 if either quantity or price is null
        }

        // Parse quantity and price to double
        try {
            int qty = Integer.parseInt(quantity.trim());
            double unitPrice = Double.parseDouble(price.trim());
            return qty * unitPrice;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // Return 0 if parsing fails
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView productcode, productname;
        Button btnget;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            productcode = itemView.findViewById(R.id.productcodee);
            productname = itemView.findViewById(R.id.productnamee);
            btnget = itemView.findViewById(R.id.btnGet);
        }
    }
}
