package com.example.masatua;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.masatua.databinding.FragmentResultBinding;
import com.example.masatua.models.Goal;
import com.example.masatua.utils.FirebaseManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ResultFragment extends Fragment {
    private FragmentResultBinding binding;
    private FirebaseManager firebaseManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //region NumberFormat
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        DecimalFormatSymbols symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(symbols);
        //endregion

        Bundle bundle = getArguments();
        if (bundle != null) {
            String totalNeededStr = bundle.getString("total_needed", "");
            String existingFund = bundle.getString("existing_fund", "");
            String monthlyInvestment = bundle.getString("monthly_investment", "");
            String returnRate = bundle.getString("return_rate", "");
            String duration = bundle.getString("duration", "");

            //region Clean and Check Input
            double totalNeeded = parseIdNumber(totalNeededStr);
            double uangAwal = parseIdNumber(existingFund);
            double investasiBulanan = parseIdNumber(monthlyInvestment);
            double returnPerTahun = parseIdNumber(returnRate) / 100;
            int tahun = parseIntSafe(duration);
            int bulan = tahun * 12;
            //endregion

            double r = returnPerTahun / 12.0;

            // FV_awal = uangAwal * (1 + r)^bulan
            double FV_awal = uangAwal * Math.pow(1 + r, bulan);

            // FV_bulanan = investasiBulanan * [((1 + r)^bulan - 1) / r]
            double FV_bulanan = investasiBulanan * (Math.pow(1 + r, bulan) - 1) / r;

            double total = Math.round(FV_awal + FV_bulanan);

            if (total < totalNeeded) {
                double kurang = totalNeeded - total;

                binding.cardResultStatus.setBackgroundResource(R.drawable.background_result_red);
                binding.ivResultAvatar.setImageResource(R.drawable.ic_avatar_sad);
                binding.tvResultMessage.setText("Strateginya belum cocok untuk mencapai mimpimu!😞");
                binding.tvTotalNeededValue.setText("Rp" + numberFormat.format(totalNeeded));
                binding.tvCurrentFundsValue.setText("Rp" + numberFormat.format(uangAwal));
                binding.tvMonthlyInvValue.setText("Rp" + numberFormat.format(investasiBulanan));
                binding.tvReturnValue.setText(formatReturnRate(returnRate) + " / Tahun");
                binding.tvDurationValue.setText(tahun + " tahun");
                binding.tvResultValue.setText("Rp" + numberFormat.format(total));
                binding.tvShortfall.setVisibility(View.VISIBLE);
                binding.tvShortfall.setText("Kurang Rp" + numberFormat.format(kurang));
                binding.layoutCtaNewStrategy.setVisibility(View.GONE);
            } else {
                binding.cardResultStatus.setBackgroundResource(R.drawable.background_result_green);
                binding.ivResultAvatar.setImageResource(R.drawable.ic_illustration);
                binding.tvResultMessage.setText("Strateginya cocok untuk mencapai mimpimu!\uD83E\uDD73");
                binding.tvTotalNeededValue.setText("Rp" + numberFormat.format(totalNeeded));
                binding.tvCurrentFundsValue.setText("Rp" + numberFormat.format(uangAwal));
                binding.tvMonthlyInvValue.setText("Rp" + numberFormat.format(investasiBulanan));
                binding.tvReturnValue.setText(formatReturnRate(returnRate) + " / Tahun");
                binding.tvDurationValue.setText(tahun + " tahun");
                binding.tvResultValue.setText("Rp" + numberFormat.format(total));
                binding.tvShortfall.setVisibility(View.GONE);
                binding.layoutCtaNewStrategy.setVisibility(View.VISIBLE);
            }
        }

        binding.cardCtaArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bundle != null) {
                    showAddGoalDialog(bundle);
                    saveNewItem(bundle);
                }
            }
        });

        return view;
    }

    // Fungsi parse Indonesia Format (SIAP tempel, gunakan dimana saja untuk angka dari UI)
    public static double parseIdNumber(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            return nf.parse(s.trim()).doubleValue();
        } catch (ParseException e) {
            return 0;
        }
    }

    // Parse int dengan pengecekan
    private int parseIntSafe(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Menambahkan tanda % jika belum ada
    private String formatReturnRate(String rate) {
        if (rate == null) return "0%";
        rate = rate.trim();
        if (rate.endsWith("%")) {
            return rate;
        } else {
            return rate + "%";
        }
    }

    private void saveNewItem(Bundle bundle) {
        String targetDanaStr = bundle.getString("total_needed", "");
        String danaSekarangStr = bundle.getString("existing_fund", "");
        String tahunTargetStr = bundle.getString("tahun_target", "");
        String investStr = bundle.getString("monthly_investment", "");
        String returnInvestStr = bundle.getString("return_rate", "");

        String namaGoals = bundle.getString("nama_goals", "").trim();
        String deskripsi = bundle.getString("deskripsi", "").trim();

        if (namaGoals.isEmpty() || targetDanaStr.isEmpty() || danaSekarangStr.isEmpty() || tahunTargetStr.isEmpty()) {
            Toast.makeText(getContext(), "Nama Goals, Target Dana, Dana Saat Ini, dan Tahun Target tidak boleh kosong", Toast.LENGTH_LONG).show();
            return;
        }

        double targetDana, danaSekarang;
        int tahunTarget;
        double invest = 0.0, returnInvest = 0.0;

        try {
            targetDana = parseIdNumber(targetDanaStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Target Dana harus angka yang valid", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            danaSekarang = parseIdNumber(danaSekarangStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Dana Saat Ini harus angka yang valid", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            tahunTarget = Integer.parseInt(tahunTargetStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Tahun Target harus angka tahun yang valid", Toast.LENGTH_LONG).show();
            return;
        }
        if (!investStr.isEmpty()) {
            try {
                invest = parseIdNumber(investStr);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invest harus angka yang valid", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (!returnInvestStr.isEmpty()) {
            try {
                returnInvest = parseIdNumber(returnInvestStr);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Return Invest harus angka yang valid", Toast.LENGTH_LONG).show();
                return;
            }
        }

        Goal goal = new Goal(namaGoals, targetDana, danaSekarang, tahunTarget, invest, returnInvest, deskripsi);

        firebaseManager.getUserGoalsCollection()
                .add(goal)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Tujuan berhasil disimpan", Toast.LENGTH_SHORT).show();
                    if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                        getParentFragmentManager().popBackStack();
                    } else {
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new GoalsListFragment())
                                .commit();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menyimpan tujuan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void showAddGoalDialog(Bundle bundle) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_data, null);

        EditText etNamaTarget = dialogView.findViewById(R.id.et_nama_target);
        EditText etDeskripsi = dialogView.findViewById(R.id.et_deskripsi);

        new AlertDialog.Builder(getContext())
                .setTitle("Tambah Goals Baru")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String namaGoals = etNamaTarget.getText().toString().trim();
                    String deskripsi = etDeskripsi.getText().toString().trim();

                    bundle.putString("nama_goals", namaGoals);
                    bundle.putString("deskripsi", deskripsi);

                    saveNewItem(bundle);
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}