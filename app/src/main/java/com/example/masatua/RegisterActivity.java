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
import com.example.masatua.utils.FirebaseManager; // Import FirebaseManager

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore; // Masih perlu import ini untuk tipe FirebaseFirestore saja

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    private FirebaseManager firebaseManager; // Gunakan FirebaseManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi FirebaseManager
        firebaseManager = FirebaseManager.getInstance();

        binding.loginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        binding.showHidePasswordIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                binding.passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.showHidePasswordIcon.setImageResource(R.drawable.ic_eye_open);
            } else {
                binding.passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                binding.showHidePasswordIcon.setImageResource(R.drawable.ic_eye_closed);
            }
            isPasswordVisible = !isPasswordVisible;
            binding.passwordInput.setSelection(binding.passwordInput.length());
        });

        binding.signUpButton.setOnClickListener(v -> registerUser());
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

        firebaseManager.getAuth().createUserWithEmailAndPassword(email, password) // Menggunakan FirebaseManager
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseManager.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        user.sendEmailVerification()
                                                .addOnCompleteListener(verifyTask -> {
                                                    if (verifyTask.isSuccessful()) {
                                                        Toast.makeText(this, "Registrasi sukses! Cek email untuk verifikasi.", Toast.LENGTH_LONG).show();

                                                        String uid = user.getUid();
                                                        User userData = new User(name, email);

                                                        firebaseManager.getFirestore().collection("users").document(uid)
                                                                .set(userData)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Log.i("RegisterActivity", "Data user berhasil disimpan di Firestore.");
                                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    finish();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Log.e("RegisterActivity", "Data user gagal disimpan di Firestore: " + e.getMessage());
                                                                    Toast.makeText(this, "Registrasi sukses, tapi gagal menyimpan data user. Silakan coba login.", Toast.LENGTH_LONG).show();
                                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                                    finish();
                                                                });
                                                    } else {
                                                        Toast.makeText(this, "Gagal mengirim email verifikasi: " + verifyTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    });
                        }
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