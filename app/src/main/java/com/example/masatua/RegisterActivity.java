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

import com.example.masatua.databinding.ActivityRegisterBinding;
import com.example.masatua.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // region LOGIC

        // region "login disini" logic
        binding.loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // endregion

        mAuth = FirebaseAuth.getInstance();

        // region Toggle password visibility
        binding.showHidePasswordIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.passwordInput.setInputType(129); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD
                binding.showHidePasswordIcon.setImageResource(R.drawable.ic_eye_open);
            } else {
                binding.passwordInput.setInputType(144); // TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//                showHidePasswordIcon.setImageResource(R.drawable.ic_eye_closed);
            }
            isPasswordVisible = !isPasswordVisible;
            binding.passwordInput.setSelection(binding.passwordInput.length());
        });
        // endregion

        binding.signUpButton.setOnClickListener(v -> registerUser());
        binding.loginLink.setOnClickListener(v -> {
            finish();
        });

        // endregion
    }

    private void registerUser() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.nameInput.setError("Nama tidak boleh kosong");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError("Email tidak boleh kosong");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError("Password tidak boleh kosong");
            return;
        }
        if (password.length() < 6) {
            binding.passwordInput.setError("Password minimal 6 karakter");
            return;
        }

        showLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(verifyTask -> {
                                        if (verifyTask.isSuccessful()) {
                                            Toast.makeText(this, "Registrasi sukses! Cek email untuk verifikasi.", Toast.LENGTH_LONG).show();
                                            // Simpan nama ke database (optional, Firestore/RealtimeDB)
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            String uid = user.getUid();
                                            User userData = new User(name, email);

                                            db.collection("users").document(uid)
                                                    .set(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.i("RegisterActivity", "Data user berhasil disimpan");
                                                        // Redirect ke login
                                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.i("RegisterActivity", "Data user gagal disimpan");
                                                    });
                                        } else {
                                            Toast.makeText(this, "Gagal mengirim email verifikasi.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
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