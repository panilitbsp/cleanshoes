package com.cleanshoes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etRepassword;
    private Button btnSignUp;
    private MaterialTextView btnSignIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();

        // Hubungkan dengan XML
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etRepassword = findViewById(R.id.et_repassword);
        btnSignIn = findViewById(R.id.btn_sign_in_text);
        btnSignUp = findViewById(R.id.btn_et_register);

        // Set password agar tampil bulat-bulat saat diketik
        etPassword.setInputType(129);
        etRepassword.setInputType(129);

        // Aksi Sign In (balik ke halaman login)
        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });

        // Aksi Sign Up (registrasi)
        btnSignUp.setOnClickListener(v -> signUpUser());
    }

    private void signUpUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repassword = etRepassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(repassword)) {
            Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proses registrasi dengan Firebase
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                        // Reset sesi biar user baru harus login lagi
                        SharedPreferences.Editor editor = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE).edit();
                        editor.clear().apply();

                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Registrasi Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
