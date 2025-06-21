package com.example.masatua;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.TokenWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.masatua.databinding.FragmentCreateGoalBinding;
import com.example.masatua.models.Goal;
import com.example.masatua.utils.FirebaseManager;

public class CreateGoalFragment extends Fragment {
    private FragmentCreateGoalBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance(); // Inisialisasi FirebaseManager
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup binding
        binding = FragmentCreateGoalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // region Back button logic
        binding.ivBackButtonBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        // endregion

        // region Save button logic
        binding.btnAddGoals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewItem();
            }
        });
        // endregion
    }

    private void saveNewItem() {
        // 1. Ambil teks dari EditText
        String namaGoals = binding.tilNamaGoals.getEditText().getText().toString().trim(); // .trim() untuk menghapus spasi di awal/akhir
        String targetDanaStr = binding.tilTargetDana.getEditText().getText().toString().trim();
        String danaSekarangStr = binding.tilDanaSaatIni.getEditText().getText().toString().trim();
        String tahunTargetStr = binding.tilTahunTarget.getEditText().getText().toString().trim();
        String investStr = binding.tilInvest.getEditText().getText().toString().trim();
        String returnInvestStr = binding.tilReturnInvest.getEditText().getText().toString().trim();
        String deskripsi = binding.tilDeskripsi.getEditText().getText().toString().trim();

        // 2. Validasi Input (penting!)
        if (namaGoals.isEmpty() || targetDanaStr.isEmpty() || danaSekarangStr.isEmpty() || tahunTargetStr.isEmpty()) {
            Toast.makeText(getContext(), "Nama Goals, Target Dana, Dana Saat Ini, dan Tahun Target tidak boleh kosong", Toast.LENGTH_LONG).show();
            return; // Hentikan proses jika ada input penting yang kosong
        }

        long targetDana;
        long danaSekarang;
        int tahunTarget;
        double invest = 0.0; // Berikan nilai default
        double returnInvest = 0.0; // Berikan nilai default

        try {
            targetDana = Long.parseLong(targetDanaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Target Dana harus angka yang valid", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            danaSekarang = Long.parseLong(danaSekarangStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Dana Saat Ini harus angka yang valid", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            tahunTarget = Integer.parseInt(tahunTargetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tahun Target harus angka tahun yang valid", Toast.LENGTH_LONG).show();
            return;
        }

        // Tangani input opsional: invest dan returnInvest
        if (!investStr.isEmpty()) {
            try {
                invest = Double.parseDouble(investStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invest harus angka yang valid", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (!returnInvestStr.isEmpty()) {
            try {
                returnInvest = Double.parseDouble(returnInvestStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Return Invest harus angka yang valid", Toast.LENGTH_LONG).show();
                return;
            }
        }


        // 3. Buat objek Goal dengan tipe data yang sesuai
        // Perhatikan: Konstruktor Goal Anda harus menerima tipe data ini.
        // Jika Goal Anda belum memiliki konstruktor ini, Anda harus membuatnya.
        Goal goal = new Goal(namaGoals, targetDana, danaSekarang, tahunTarget, invest, returnInvest, deskripsi);

        firebaseManager.getUserGoalsCollection() // Pastikan ini mengarah ke koleksi 'goals' atau 'items' yang benar
                .add(goal)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Tujuan berhasil disimpan", Toast.LENGTH_SHORT).show();

                    // Kembali ke fragment sebelumnya atau tampilkan daftar goals
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        // Ini mungkin tidak perlu jika GoalsListFragment selalu ada di back stack
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new GoalsListFragment())
                                .commit();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menyimpan tujuan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}