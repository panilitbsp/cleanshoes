package com.cleanshoes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private Button btnSignIn;
    private MaterialTextView btnSignUp;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "loginPrefs";
    private static final String KEY_REMEMBER = "rememberMe";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final long SESSION_DURATION = 3600000; // 1 jam dalam milidetik

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Hubungkan dengan XML
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        cbRememberMe = findViewById(R.id.cb_keep_logged_in);
        btnSignIn = findViewById(R.id.btn_et_login);
        btnSignUp = findViewById(R.id.btn_sign_up_text);

        // Set password agar tampil bulat-bulat saat diketik
        etPassword.setInputType(129);

        // Buat email bisa di-select all dengan CMD+A / Ctrl+A
        etEmail.setOnKeyListener((v, keyCode, event) -> {
            if (event.isCtrlPressed() && keyCode == KeyEvent.KEYCODE_A) {
                etEmail.selectAll();
                return true;
            }
            return false;
        });

        // Cek apakah remember me aktif, usernya sama, dan sesi masih berlaku
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, null);
        long lastLoginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER, false);

        if (rememberMe && savedEmail != null && System.currentTimeMillis() - lastLoginTime < SESSION_DURATION) {
            navigateToHome();
        }

        // Aksi Sign In
        btnSignIn.setOnClickListener(v -> signInUser());

        // Aksi Sign Up
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signInUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignInActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                        // Simpan preferensi dan sesi user
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (cbRememberMe.isChecked()) {
                            editor.putBoolean(KEY_REMEMBER, true);
                            editor.putString(KEY_EMAIL, email);
                            editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
                        } else {
                            editor.clear();
                        }
                        editor.apply();

                        navigateToHome();
                    } else {
                        Toast.makeText(SignInActivity.this, "Login Gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
