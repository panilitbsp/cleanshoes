package com.cleanshoes;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OrderActivity extends AppCompatActivity {

    private TextView orderStatus, estimatedDays, orderCode, receiverName, mobileNumber, addressView, paymentMethod, orderTotal;
    private ImageView statusIconProcess, statusIconWait, statusIconDeliver, statusIconReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Bind view dengan ID di XML
        orderStatus = findViewById(R.id.orderStatus);
        estimatedDays = findViewById(R.id.estimatedDays);
        orderCode = findViewById(R.id.orderCode);
        receiverName = findViewById(R.id.receiverName);
        mobileNumber = findViewById(R.id.mobileNumber);
        addressView = findViewById(R.id.address);
        paymentMethod = findViewById(R.id.paymentMethod);
        orderTotal = findViewById(R.id.orderTotal);

        statusIconProcess = findViewById(R.id.statusIconProcess);
        statusIconWait = findViewById(R.id.statusIconWait);
        statusIconDeliver = findViewById(R.id.statusIconDeliver);
        statusIconReceived = findViewById(R.id.statusIconReceived);

        // Bottom Navigation
        ImageButton navHome = findViewById(R.id.navHome);
        ImageButton navOrder = findViewById(R.id.navOrder);
        ImageButton navChat = findViewById(R.id.navChat);
        ImageButton navAccount = findViewById(R.id.navAccount);

        navHome.setOnClickListener(v -> startActivity(new Intent(OrderActivity.this, HomeActivity.class)));
        navOrder.setOnClickListener(v -> startActivity(new Intent(OrderActivity.this, OrderActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(OrderActivity.this, ChatActivity.class)));
        navAccount.setOnClickListener(v -> startActivity(new Intent(OrderActivity.this, AccountActivity.class)));

        // Ambil data dari intent
        Intent intent = getIntent();
        String contactName = intent.getStringExtra("contactName");
        String contactPhoneNumber = intent.getStringExtra("contactPhoneNumber");
        String notes = intent.getStringExtra("notes");
        String address = intent.getStringExtra("address");
        String paymentMethodValue = intent.getStringExtra("paymentMethod");
        int totalPrice = intent.getIntExtra("total", 0);
        String orderCodeValue = intent.getStringExtra("orderCode");

        // Set data ke tampilan dengan pengecekan null
        orderCode.setText(orderCodeValue != null ? orderCodeValue : "N/A");
        receiverName.setText(contactName != null ? contactName : "N/A");
        mobileNumber.setText(contactPhoneNumber != null ? contactPhoneNumber : "N/A");

        if (address != null && !address.isEmpty()) {
            // Cuma tampilkan alamat (tanpa link GMaps)
            String[] addressParts = address.split("\n");
            addressView.setText(addressParts[0]);
        } else {
            addressView.setText("Pickup at Store");
        }

        paymentMethod.setText(paymentMethodValue != null ? paymentMethodValue : "Not Specified");
        orderTotal.setText("Rp " + totalPrice);


        // Countdown untuk estimasi waktu
        long countdownMillis = 3 * 24 * 60 * 60 * 1000; // 3 hari
        new CountDownTimer(countdownMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long days = millisUntilFinished / (1000 * 60 * 60 * 24);
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                estimatedDays.setText("Estimated Delivery: " + days + "d " + hours + "h " + minutes + "m");
            }

            @Override
            public void onFinish() {
                estimatedDays.setText("Delivered");
            }
        }.start();

        // Simulasi status perubahan
        updateOrderStatus(1);
    }

    private void updateOrderStatus(int status) {
        switch (status) {
            case 1:
                orderStatus.setText("Order Status: In Progress");
                statusIconProcess.setImageResource(R.drawable.ic_process_on);
                break;
            case 2:
                orderStatus.setText("Order Status: Waiting for Pickup");
                statusIconWait.setImageResource(R.drawable.ic_wait_on);
                break;
            case 3:
                orderStatus.setText("Order Status: Out for Delivery");
                statusIconDeliver.setImageResource(R.drawable.ic_otw_on);
                break;
            case 4:
                orderStatus.setText("Order Status: Received");
                statusIconReceived.setImageResource(R.drawable.ic_received_on);
                break;
        }
    }
}
