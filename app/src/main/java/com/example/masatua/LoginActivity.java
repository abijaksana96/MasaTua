package com.example.masatua;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.masatua.databinding.ActivityLoginBinding;
import com.example.masatua.models.User;
import com.example.masatua.utils.FirebaseManager; // Import FirebaseManager

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseManager firebaseManager; // Gunakan FirebaseManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi FirebaseManager
        firebaseManager = FirebaseManager.getInstance();

        // Check if user is already logged in and verified
        FirebaseUser currentUser = firebaseManager.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        binding.signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void loginUser() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError("Email tidak boleh kosong");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError("Password tidak boleh kosong");
            return;
        }

        showLoading(true);

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password) // Menggunakan FirebaseManager
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseManager.getCurrentUser(); // Menggunakan FirebaseManager
                        if (user != null && user.isEmailVerified()) {
                            Log.d("LoginActivity", "User logged in and email verified. UID: " + user.getUid());
                            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            firebaseManager.signOutUser(); // Menggunakan FirebaseManager
                            Toast.makeText(this, "Email belum diverifikasi. Cek inbox email Anda.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetPassword() {
        String email = binding.emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError("Masukkan email terlebih dahulu");
            return;
        }

        showLoading(true);

        firebaseManager.getAuth().sendPasswordResetEmail(email) // Menggunakan FirebaseManager
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Link reset password telah dikirim ke email.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Gagal mengirim reset password: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        View overlayView = findViewById(R.id.overlayView);
        if (show) {
            overlayView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            overlayView.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}