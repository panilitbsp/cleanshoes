package com.cleanshoes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup Navbar
        setupNavbar();

        // Setup Shoe Buttons
        setupShoeButtons();
    }

    private void setupNavbar() {
        ImageButton navHome = findViewById(R.id.navHome);
        ImageButton navOrder = findViewById(R.id.navOrder);
        ImageButton navChat = findViewById(R.id.navChat);
        ImageButton navAccount = findViewById(R.id.navAccount);

        navHome.setOnClickListener(v -> {
            Toast.makeText(this, "You are already on Home", Toast.LENGTH_SHORT).show();
        });

        navOrder.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navChat.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void setupShoeButtons() {
        findViewById(R.id.btnSneakers).setOnClickListener(v -> openShoeDetail("Sneakers"));
        findViewById(R.id.btnHeels).setOnClickListener(v -> openShoeDetail("Heels"));
        findViewById(R.id.btnLoafer).setOnClickListener(v -> openShoeDetail("Loafer"));
        findViewById(R.id.btnBoots).setOnClickListener(v -> openShoeDetail("Boots"));
        findViewById(R.id.btnSuede).setOnClickListener(v -> openShoeDetail("Suede"));
        findViewById(R.id.btnCrocs).setOnClickListener(v -> openShoeDetail("Crocs"));
    }

    // HomeActivity.java
    private void openShoeDetail(String shoeType) {
        Toast.makeText(this, "Selected: " + shoeType, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, PickActivity.class);
        intent.putExtra("shoeType", shoeType);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String shoeType = data.getStringExtra("shoeType");
            String selectedTreatments = data.getStringExtra("selectedTreatments");
            int totalPrice = data.getIntExtra("totalPrice", 0);
            Toast.makeText(this, "Shoe: " + shoeType + "\nTreatments: " + selectedTreatments + "\nTotal Price: " + totalPrice, Toast.LENGTH_LONG).show();
        }
    }
}
