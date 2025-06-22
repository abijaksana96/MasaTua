package com.example.masatua;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masatua.databinding.FragmentHomeBinding;
import com.example.masatua.utils.FirebaseManager;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseManager firebaseManager;
    private FirebaseUser user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
        user = firebaseManager.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //region Setup binding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
        //endregion
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (user != null) {
            String namaUser = user.getDisplayName();
            if (namaUser == null || namaUser.isEmpty()) {
                // Coba ambil dari Firestore jika displayName kosong
                firebaseManager.getCurrentUserDocument()
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String namaFirestore = documentSnapshot.getString("nama");
                                if (namaFirestore != null && !namaFirestore.isEmpty()) {
                                    binding.tvUsername.setText("Hi, Namaku \n" + namaFirestore);
                                } else {
                                    binding.tvUsername.setText("Hi, Namaku \nUser tanpa nama");
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            binding.tvUsername.setText("Hi, Namaku \nUser tanpa nama");
                        });
            } else {
                binding.tvUsername.setText("Hi, Namaku \n" + namaUser);
            }
        } else {
            binding.tvUsername.setText("Hi, Namaku \nUser tidak login");
        }
    }
}