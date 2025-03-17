package com.cleanshoes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Ambil data dari intent
        Intent intent = getIntent();
        String contactName = intent.getStringExtra("contactName");
        String contactPhoneNumber = intent.getStringExtra("contactPhoneNumber");
        String notes = intent.getStringExtra("notes");
        String address = intent.getStringExtra("address");
        String paymentMethodValue = intent.getStringExtra("paymentMethod");
        int totalPrice = intent.getIntExtra("total", 0); // Ambil total harga dengan kunci "total"

        // Bind view dengan ID di XML
        TextView orderCode = findViewById(R.id.orderCode);
        TextView receiverName = findViewById(R.id.receiverName);
        TextView mobileNumber = findViewById(R.id.mobileNumber);
        TextView addressView = findViewById(R.id.address);
        TextView paymentMethod = findViewById(R.id.paymentMethod);
        TextView orderTotal = findViewById(R.id.orderTotal);
        Button trackOrderButton = findViewById(R.id.trackOrderButton);

        // Set data ke tampilan
        orderCode.setText(generateOrderCode());
        receiverName.setText(contactName);
        mobileNumber.setText(contactPhoneNumber);
        // atur tulisan alamat pengantaran
        if (address != null && !address.isEmpty()) {
            addressView.setText(address);
            // Tambahkan klik agar link GMaps bisa dibuka langsung
            addressView.setOnClickListener(v -> {
                String[] addressParts = address.split("\n");
                if (addressParts.length > 1) {
                    String gmapsLink = addressParts[1];
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(gmapsLink));
                    startActivity(browserIntent);
                }
            });
        } else {
            addressView.setText("Pickup at Store");
        }


//        kalo ini alamat yang gapake link gmaps:
//        if (address != null && !address.isEmpty()) {
//            // Cuma tampilkan alamat (tanpa link GMaps)
//            String[] addressParts = address.split("\n");
//            addressView.setText(addressParts[0]);
//        } else {
//            addressView.setText("Pickup at Store");
//        }


        // tulisan method payment
        paymentMethod.setText(paymentMethodValue != null ? paymentMethodValue : "Not Specified");
        orderTotal.setText("Rp " + totalPrice);

        // Tombol lacak pesanan
        trackOrderButton.setOnClickListener(v -> {
            Intent orderHistoryIntent = new Intent(OrderConfirmationActivity.this, OrderActivity.class);
            orderHistoryIntent.putExtra("contactName", contactName);
            orderHistoryIntent.putExtra("contactPhoneNumber", contactPhoneNumber);
            orderHistoryIntent.putExtra("notes", notes);
            orderHistoryIntent.putExtra("address", address);
            orderHistoryIntent.putExtra("paymentMethod", paymentMethodValue);
            orderHistoryIntent.putExtra("total", totalPrice);
            orderHistoryIntent.putExtra("orderCode", orderCode.getText().toString());
            startActivity(orderHistoryIntent);
            finish();
        });

    }

    // Fungsi sederhana buat generate kode order (random 6 digit)
    private String generateOrderCode() {
        int randomCode = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomCode);
    }
}
