package com.example.masatua;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.masatua.databinding.FragmentProfileBinding;
import com.example.masatua.utils.FirebaseManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        firebaseManager = FirebaseManager.getInstance();
        user = firebaseManager.getCurrentUser();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEditMode(false); // Default: non-edit mode

        // Tombol Edit: Masuk ke mode edit
        binding.btnEditProfile.setOnClickListener(v -> setEditMode(true));

        // Tombol Batal: Kembali ke mode non-edit
        binding.btnCancel.setOnClickListener(v -> {
            setEditMode(false);
            loadProfile(); // Kembalikan data ke semula
        });

        // Tombol Simpan: update profile di sini
        binding.btnSave.setOnClickListener(v -> {
            String namaBaru = binding.etFullName.getText().toString().trim();
            String passwordBaru = binding.etPassword.getText().toString().trim();

            // Update nama di Firestore
            firebaseManager.getCurrentUserDocument()
                    .update("name", namaBaru)
                    .addOnSuccessListener(aVoid -> {
                        // (Opsional) update displayName di Firebase Auth
                        FirebaseUser firebaseUser = firebaseManager.getCurrentUser();
                        if (firebaseUser != null) {
                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(namaBaru)
                                            .build();
                            firebaseUser.updateProfile(profileUpdates);

                            // Update password jika diisi (dan panjang cukup)
                            if (!passwordBaru.isEmpty() && passwordBaru.length() >= 6) {
                                firebaseUser.updatePassword(passwordBaru)
                                        .addOnSuccessListener(aVoid2 -> {
                                            // Sukses update password di Auth
                                            // Update info password di Firestore (timestamp, bukan password asli!)
                                            firebaseManager.getCurrentUserDocument()
                                                    .update("password_updated_at", FieldValue.serverTimestamp())
                                                    .addOnSuccessListener(aVoid3 -> {
                                                        // Berhasil update info password di Firestore
                                                        Toast.makeText(getContext(), "Password berhasil diubah", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // Gagal update info password di Firestore
                                                        Toast.makeText(getContext(), "Password diubah, tapi gagal update info di database.", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            // Gagal update password (misal: user harus re-auth)
                                            Toast.makeText(getContext(), "Gagal mengubah password: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            }
                        }

                        setEditMode(false);
                        loadProfile();
                    })
                    .addOnFailureListener(e -> {
                        // Gagal update Firestore
                        Toast.makeText(getContext(), "Gagal update profil: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // Tombol Logout: Keluar dari akun dan kembali ke halaman login/main
        binding.btnLogout.setOnClickListener(v -> {
            firebaseManager.signOutUser();
            Toast.makeText(getContext(), "Berhasil logout", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        loadProfile();
    }

    /** Menampilkan data profil */
    private void loadProfile() {
        if (user != null) {
            String namaUser = user.getDisplayName();
            if (namaUser == null || namaUser.isEmpty()) {
                firebaseManager.getCurrentUserDocument()
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String namaFirestore = documentSnapshot.getString("name");
                            binding.etFullName.setText((namaFirestore != null && !namaFirestore.isEmpty()) ? namaFirestore : "User tanpa nama");
                            String emailFirestore = documentSnapshot.getString("email");
                            binding.etEmail.setText((emailFirestore != null && !emailFirestore.isEmpty()) ? emailFirestore : user.getEmail());
                        })
                        .addOnFailureListener(e -> {
                            binding.etFullName.setText("User tanpa nama");
                            binding.etEmail.setText(user.getEmail() != null ? user.getEmail() : "Email tidak ditemukan");
                        });
            } else {
                binding.etFullName.setText(namaUser);
                binding.etEmail.setText(user.getEmail() != null ? user.getEmail() : "Email tidak ditemukan");
            }
            // password field: tetap kosong atau placeholder
            binding.etPassword.setText("********");
        } else {
            binding.etFullName.setText("User tidak login");
            binding.etEmail.setText("User tidak login");
            binding.etPassword.setText("");
        }
    }

    /** Mode Edit/non-Edit: true=edit, false=non-edit */
    private void setEditMode(boolean editMode) {
        binding.etFullName.setEnabled(editMode);
        binding.etPassword.setEnabled(editMode); // hanya enable jika ingin ubah sandi
        binding.layoutEditButtons.setVisibility(editMode ? View.VISIBLE : View.GONE);
        binding.btnEditProfile.setVisibility(editMode ? View.GONE : View.VISIBLE);
    }
}