package com.cleanshoes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class PickActivity extends AppCompatActivity {

    private String shoeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick);

        // Ambil data dari intent
        Intent intent = getIntent();
        shoeType = intent.getStringExtra("shoeType");

        // Tampilkan jenis sepatu di UI
        TextView shoeTypeTextView = findViewById(R.id.shoeTypeTextView);
        shoeTypeTextView.setText(shoeType);

        // Setup RadioGroup untuk Level Kotoran
        RadioGroup radioGroupDirtiness = findViewById(R.id.radioGroupDirtiness);

        // Setup Checkbox
        CheckBox regularCleaning = findViewById(R.id.checkboxRegular);
        CheckBox fastCleaning = findViewById(R.id.checkboxFast);
        CheckBox unyellowing = findViewById(R.id.checkboxUnyellowing);
        CheckBox repaint = findViewById(R.id.checkboxRepaint);
        CheckBox reglue = findViewById(R.id.checkboxReglue);

        CheckBox[] checkboxes = {regularCleaning, fastCleaning, unyellowing, repaint, reglue};

        // Batasan Maksimal 3 Checkbox
        for (CheckBox checkBox : checkboxes) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int checkedCount = 0;
                for (CheckBox cb : checkboxes) {
                    if (cb.isChecked()) {
                        checkedCount++;
                    }
                }

                // Kalau lebih dari 3, uncheck checkbox yang baru di-klik dan kasih peringatan
                if (checkedCount > 3) {
                    checkBox.setChecked(false);
                    Toast.makeText(this, "You can select up to 3 treatments only", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button addToCartButton = findViewById(R.id.addToCartButton);
        Button editButton = findViewById(R.id.editButton);

        // Event untuk tombol Edit supaya balik ke halaman Home
        editButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(PickActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        });

        addToCartButton.setOnClickListener(v -> {
            // Cek apakah user sudah memilih level kotoran
            int selectedDirtinessId = radioGroupDirtiness.getCheckedRadioButtonId();
            if (selectedDirtinessId == -1) {
                Toast.makeText(this, "Please select a level of dirtiness", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> selectedTreatments = new ArrayList<>();
            ArrayList<Integer> treatmentPrices = new ArrayList<>();
            int totalPrice = 0;
            int dirtinessPrice = 0;
            String dirtinessLevel = "";

            // Tambahkan harga berdasarkan level kotoran yang dipilih
            if (selectedDirtinessId == R.id.radioLightDirty) {
                dirtinessPrice = 20000;
                dirtinessLevel = "Light Dirty";
            } else if (selectedDirtinessId == R.id.radioRegularDirty) {
                dirtinessPrice = 30000;
                dirtinessLevel = "Medium Dirty";
            } else if (selectedDirtinessId == R.id.radioHeavyDirty) {
                dirtinessPrice = 40000;
                dirtinessLevel = "Heavy Dirty";
            } else if (selectedDirtinessId == R.id.radioBadDirty) {
                dirtinessPrice = 50000;
                dirtinessLevel = "Bad Dirty";
            }
            totalPrice += dirtinessPrice;

            // Cek checkbox yang dipilih
            if (regularCleaning.isChecked()) {
                selectedTreatments.add("Regular Cleaning");
                treatmentPrices.add(0);
            }
            if (fastCleaning.isChecked()) {
                selectedTreatments.add("Fast Cleaning");
                treatmentPrices.add(5000);
                totalPrice += 5000;
            }
            if (unyellowing.isChecked()) {
                selectedTreatments.add("Unyellowing Package");
                treatmentPrices.add(15000);
                totalPrice += 15000;
            }
            if (repaint.isChecked()) {
                selectedTreatments.add("Repaint");
                treatmentPrices.add(15000);
                totalPrice += 15000;
            }
            if (reglue.isChecked()) {
                selectedTreatments.add("Re-glue");
                treatmentPrices.add(15000);
                totalPrice += 15000;
            }

            if (selectedTreatments.isEmpty()) {
                Toast.makeText(this, "Please select at least one treatment", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim data ke ShippingActivity
            Intent shippingIntent = new Intent(PickActivity.this, ShippingActivity.class);
            shippingIntent.putExtra("shoeType", shoeType);
            shippingIntent.putExtra("dirtinessLevel", dirtinessLevel);
            shippingIntent.putExtra("dirtinessPrice", dirtinessPrice);
            shippingIntent.putStringArrayListExtra("selectedTreatments", selectedTreatments);
            shippingIntent.putIntegerArrayListExtra("treatmentPrices", treatmentPrices);
            shippingIntent.putExtra("totalPrice", totalPrice);
            startActivity(shippingIntent);
        });
    }
}
