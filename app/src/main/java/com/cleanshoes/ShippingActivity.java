package com.cleanshoes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ShippingActivity extends AppCompatActivity {

    private EditText notesInput, contactNameInput, contactPhoneInput;
    private TextView locationText;
    private ActivityResultLauncher<Intent> mapLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        // Bind views
        RadioGroup deliveryMethodGroup = findViewById(R.id.deliveryMethodGroup);
        notesInput = findViewById(R.id.notesInput);
        contactNameInput = findViewById(R.id.contactName);
        contactPhoneInput = findViewById(R.id.contactPhoneNumber);
        locationText = findViewById(R.id.locationText);
        LinearLayout itemListContainer = findViewById(R.id.itemList);
        TextView totalPriceView = findViewById(R.id.totalPrice);
        Spinner paymentMethod = findViewById(R.id.paymentMethod);
        Button placeOrderButton = findViewById(R.id.placeOrderButton);
        Button selectLocationButton = findViewById(R.id.selectLocationButton);

        // Setup map launcher untuk alamat lengkap, koordinat, dan link GMaps
        mapLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                String location = result.getData().getStringExtra("location");
                String address = result.getData().getStringExtra("address");

                // Buat link GMaps
                String gmapsLink = "https://www.google.com/maps?q=" + location;

                // Tampilkan alamat lengkap, dan link gmaps
                String displayText = (address != null ? address : "Not available") + "\n" + gmapsLink;
                locationText.setText(displayText);

                // Tambahkan klik untuk buka link
                locationText.setOnClickListener(v -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(gmapsLink));
                    startActivity(browserIntent);
                });
            }
        });

        // Handle Delivery Method
        deliveryMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.deliveryOption) {
                selectLocationButton.setVisibility(View.VISIBLE);
                locationText.setVisibility(View.VISIBLE);
            } else {
                selectLocationButton.setVisibility(View.GONE);
                locationText.setVisibility(View.GONE);
            }
        });

        // Launch map for location selection
        selectLocationButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(ShippingActivity.this, MapsActivity.class);
            mapLauncher.launch(mapIntent);
        });

        // Get data from intent
        Intent intent = getIntent();
        String shoeType = intent.getStringExtra("shoeType");
        String dirtinessLevel = intent.getStringExtra("dirtinessLevel");
        int dirtinessPrice = intent.getIntExtra("dirtinessPrice", 0);
        ArrayList<String> selectedTreatments = intent.getStringArrayListExtra("selectedTreatments");
        ArrayList<Integer> treatmentPrices = intent.getIntegerArrayListExtra("treatmentPrices");
        int total = dirtinessPrice;

        // Map shoeType to corresponding image
        int shoeImageResId = getShoeImageResId(shoeType);

        // Add dirtiness level to item list
        View dirtinessView = getLayoutInflater().inflate(R.layout.item_summary, itemListContainer, false);
        ImageView shoeImage = dirtinessView.findViewById(R.id.shoeImage);
        TextView shoeName = dirtinessView.findViewById(R.id.shoeName);
        TextView treatmentName = dirtinessView.findViewById(R.id.treatmentName);
        TextView treatmentPrice = dirtinessView.findViewById(R.id.treatmentPrice);

        shoeImage.setImageResource(shoeImageResId);
        shoeName.setText(shoeType);
        treatmentName.setText("Level: " + dirtinessLevel);
        treatmentPrice.setText("Rp " + dirtinessPrice);
        itemListContainer.addView(dirtinessView);

        // Populate item list
        if (selectedTreatments != null && treatmentPrices != null) {
            for (int i = 0; i < selectedTreatments.size(); i++) {
                View itemView = getLayoutInflater().inflate(R.layout.item_summary, itemListContainer, false);
                ImageView treatmentImage = itemView.findViewById(R.id.shoeImage);
                TextView treatmentNameView = itemView.findViewById(R.id.treatmentName);
                TextView treatmentPriceView = itemView.findViewById(R.id.treatmentPrice);

                treatmentImage.setImageResource(shoeImageResId);
                treatmentNameView.setText(selectedTreatments.get(i));
                treatmentPriceView.setText("Rp " + treatmentPrices.get(i));
                total += treatmentPrices.get(i);

                itemListContainer.addView(itemView);
            }
        }

        // Set total price
        totalPriceView.setText("Total: Rp " + total);

        // Setup Payment Method Spinner
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                this, R.array.payment_methods, android.R.layout.simple_spinner_item);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethod.setAdapter(paymentAdapter);

        // Place Order Button
        int finalTotal = total;
        placeOrderButton.setOnClickListener(v -> {
            Intent confirmationIntent = new Intent(ShippingActivity.this, OrderConfirmationActivity.class);
            confirmationIntent.putExtra("contactName", contactNameInput.getText().toString());
            confirmationIntent.putExtra("contactPhoneNumber", contactPhoneInput.getText().toString());
            confirmationIntent.putExtra("notes", notesInput.getText().toString());
            confirmationIntent.putExtra("address", locationText.getText().toString()); // Kirim alamat
            // Ambil metode pembayaran yang dipilih
            String selectedPaymentMethod = paymentMethod.getSelectedItem().toString();
            confirmationIntent.putExtra("paymentMethod", selectedPaymentMethod);
            confirmationIntent.putExtra("total", finalTotal); // Kirim total harga
            startActivity(confirmationIntent);
        });
    }

    private int getShoeImageResId(String shoeType) {
        switch (shoeType.toLowerCase()) {
            case "boots": return R.drawable.boots;
            case "crocs": return R.drawable.crocs;
            case "heels": return R.drawable.heels;
            case "loafer": return R.drawable.loafer;
            case "sneakers": return R.drawable.sneakers;
            case "suede": return R.drawable.suede;
            default: return R.drawable.sneakers;
        }
    }
}
