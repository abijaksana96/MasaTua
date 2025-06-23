package com.example.masatua;

import android.health.connect.datatypes.StepsCadenceRecord;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masatua.databinding.FragmentCalculatorBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CalculatorFragment extends Fragment {
    private FragmentCalculatorBinding binding;
    private boolean isBulkProgrammaticChange = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup binding
        binding = FragmentCalculatorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //region NumberFormat
        // Buat NumberFormat
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        DecimalFormatSymbols symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(symbols);
        //endregion

        binding.etExpenseMonthly.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isBulkProgrammaticChange) return;

                String inputStr = s.toString().trim();

                // Jika kosong, reset field & tampilkan 0
                if (inputStr.isEmpty()) {
                    isBulkProgrammaticChange = true;
                    binding.tvExpenseYearly.setText("0");
                    binding.etExpenseMonthly.setError(null); // hilangkan error jika input kosong
                    // Reset/clear hanya jika sebelumnya ada isian
                    binding.tvExpenseYearlyLabel.setVisibility(View.GONE);
                    binding.layoutExpenseYearly.setVisibility(View.GONE);
                    binding.tvAgeNowLabel.setVisibility(View.GONE);
                    binding.etAgeNow.setVisibility(View.GONE);
                    binding.layoutAgeNow.setVisibility(View.GONE);
                    binding.tvRetirementAgeLabel.setVisibility(View.GONE);
                    binding.layoutRetirementAge.setVisibility(View.GONE);
                    binding.tvInflationRateLabel.setVisibility(View.GONE);
                    binding.layoutInflationRate.setVisibility(View.GONE);
                    binding.tvExpenseYearlyRetirementLabel.setVisibility(View.GONE);
                    binding.layoutExpenseYearlyRetirement.setVisibility(View.GONE);
                    binding.tvMonthlyInvestmentNeededLabel.setVisibility(View.GONE);
                    binding.layoutMonthlyInvestmentNeeded.setVisibility(View.GONE);
                    binding.tvInvestmentReturnLabel.setVisibility(View.GONE);
                    binding.layoutInvestmentReturn.setVisibility(View.GONE);
                    binding.tvAgeNeededLabel.setVisibility(View.GONE);
                    binding.layoutAgeNeeded.setVisibility(View.GONE);
                    binding.etAgeNow.setText("");
                    binding.etRetirementAge.setText("");
                    binding.etInflationRate.setText("");
                    binding.etMonthlyInvestment.setText("");
                    binding.etInvestmentReturn.setText("");
                    isBulkProgrammaticChange = false;
                    return;
                }

                double inputDouble = parseIdNumber(inputStr);

                // Jika hanya angka 0, error dan reset field
                if (inputDouble == 0.0) {
                    isBulkProgrammaticChange = true;
                    binding.tvExpenseYearly.setText("0");
                    binding.etExpenseMonthly.setError("Angka harus lebih dari 0");
                    binding.tvExpenseYearlyLabel.setVisibility(View.GONE);
                    binding.layoutExpenseYearly.setVisibility(View.GONE);
                    binding.tvAgeNowLabel.setVisibility(View.GONE);
                    binding.etAgeNow.setVisibility(View.GONE);
                    binding.layoutAgeNow.setVisibility(View.GONE);
                    binding.tvRetirementAgeLabel.setVisibility(View.GONE);
                    binding.layoutRetirementAge.setVisibility(View.GONE);
                    binding.tvInflationRateLabel.setVisibility(View.GONE);
                    binding.layoutInflationRate.setVisibility(View.GONE);
                    binding.tvExpenseYearlyRetirementLabel.setVisibility(View.GONE);
                    binding.layoutExpenseYearlyRetirement.setVisibility(View.GONE);
                    binding.tvMonthlyInvestmentNeededLabel.setVisibility(View.GONE);
                    binding.layoutMonthlyInvestmentNeeded.setVisibility(View.GONE);
                    binding.tvInvestmentReturnLabel.setVisibility(View.GONE);
                    binding.layoutInvestmentReturn.setVisibility(View.GONE);
                    binding.tvAgeNeededLabel.setVisibility(View.GONE);
                    binding.layoutAgeNeeded.setVisibility(View.GONE);
                    binding.etAgeNow.setText("");
                    binding.etRetirementAge.setText("");
                    binding.etInflationRate.setText("");
                    binding.etMonthlyInvestment.setText("");
                    binding.etInvestmentReturn.setText("");
                    isBulkProgrammaticChange = false;
                    return;
                }

                try {
                    if (inputDouble > 0) {
                        binding.etExpenseMonthly.setError(null);
                        binding.tvExpenseYearlyLabel.setVisibility(View.VISIBLE);
                        binding.layoutExpenseYearly.setVisibility(View.VISIBLE);

                        binding.tvAgeNowLabel.setVisibility(View.VISIBLE);
                        binding.etAgeNow.setVisibility(View.VISIBLE);
                        binding.layoutAgeNow.setVisibility(View.VISIBLE);

                        Double expenseYearly = inputDouble * 12;
                        String formatted = numberFormat.format(expenseYearly);

                        binding.tvExpenseYearly.setText(formatted);

                        //region Set ExpenseYearlyRetirement
                        if (!binding.tvExpenseYearlyRetirement.getText().toString().isEmpty()) {
                            setFutureValue();
                        }
                        //endregion
                    } else {
                        isBulkProgrammaticChange = true;
                        binding.etExpenseMonthly.setError("Angka harus lebih dari 0");
                        binding.tvExpenseYearly.setText("0");
                        binding.tvExpenseYearlyLabel.setVisibility(View.GONE);
                        binding.layoutExpenseYearly.setVisibility(View.GONE);
                        binding.tvAgeNowLabel.setVisibility(View.GONE);
                        binding.etAgeNow.setVisibility(View.GONE);
                        binding.layoutAgeNow.setVisibility(View.GONE);
                        binding.tvRetirementAgeLabel.setVisibility(View.GONE);
                        binding.layoutRetirementAge.setVisibility(View.GONE);
                        binding.tvInflationRateLabel.setVisibility(View.GONE);
                        binding.layoutInflationRate.setVisibility(View.GONE);
                        binding.tvExpenseYearlyRetirementLabel.setVisibility(View.GONE);
                        binding.layoutExpenseYearlyRetirement.setVisibility(View.GONE);
                        binding.tvMonthlyInvestmentNeededLabel.setVisibility(View.GONE);
                        binding.layoutMonthlyInvestmentNeeded.setVisibility(View.GONE);
                        binding.tvInvestmentReturnLabel.setVisibility(View.GONE);
                        binding.layoutInvestmentReturn.setVisibility(View.GONE);
                        binding.tvAgeNeededLabel.setVisibility(View.GONE);
                        binding.layoutAgeNeeded.setVisibility(View.GONE);
                        binding.etAgeNow.setText("");
                        binding.etRetirementAge.setText("");
                        binding.etInflationRate.setText("");
                        binding.etMonthlyInvestment.setText("");
                        binding.etInvestmentReturn.setText("");
                        isBulkProgrammaticChange = false;
                    }
                } catch (NumberFormatException e) {
                    isBulkProgrammaticChange = true;
                    binding.tvExpenseYearly.setText("0");
                    binding.etExpenseMonthly.setError("Input tidak valid");
                    isBulkProgrammaticChange = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isBulkProgrammaticChange) return;
                if (!s.toString().equals(current)) {
                    binding.etExpenseMonthly.removeTextChangedListener(this);

                    // Hanya izinkan angka, koma, dan titik
                    String cleanString = s.toString().replaceAll("[^\\d.,]", "");
                    if (cleanString.isEmpty()) {
                        current = "";
                        binding.etExpenseMonthly.setText("");
                        binding.etExpenseMonthly.addTextChangedListener(this);
                        return;
                    }
                    double parsed = parseIdNumber(cleanString);

                    // Format ke 1.000.000,00
                    String formatted = numberFormat.format(parsed);

                    current = formatted;
                    binding.etExpenseMonthly.setText(formatted);
                    binding.etExpenseMonthly.setSelection(formatted.length());

                    binding.etExpenseMonthly.addTextChangedListener(this);
                }
            }
        });

        binding.etAgeNow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();

                if (!input.isEmpty()) {
                    binding.tvRetirementAgeLabel.setVisibility(View.VISIBLE);
                    binding.layoutRetirementAge.setVisibility(View.VISIBLE);
                } else {
                    binding.tvRetirementAgeLabel.setVisibility(View.GONE);
                    binding.layoutRetirementAge.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etRetirementAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();
                String ageNowStr = binding.etAgeNow.getText().toString().trim();
                if (input.isEmpty() || ageNowStr.isEmpty()) {
                    // Jangan lanjut jika salah satu kosong
                    binding.tvInflationRateLabel.setVisibility(View.GONE);
                    binding.layoutInflationRate.setVisibility(View.GONE);
                    return;
                }

                int ageNow = Integer.parseInt(ageNowStr);

                if (!input.isEmpty()) {
                    if (Integer.parseInt(input) <= ageNow) {
                        binding.etRetirementAge.setError("Usia pensiun harus lebih besar dari usiamu sekarang");

                        binding.tvInflationRateLabel.setVisibility(View.GONE);
                        binding.layoutInflationRate.setVisibility(View.GONE);
                    } else {
                        binding.tvInflationRateLabel.setVisibility(View.VISIBLE);
                        binding.layoutInflationRate.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etInflationRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inflationRateStr = s.toString().trim();
                if (!inflationRateStr.isEmpty()) {
                    binding.tvExpenseYearlyRetirementLabel.setVisibility(View.VISIBLE);
                    binding.layoutExpenseYearlyRetirement.setVisibility(View.VISIBLE);
                    binding.layoutTotalNeeded.setVisibility(View.VISIBLE);
                    binding.tvTotalNeededLabel.setVisibility(View.VISIBLE);
                    binding.tvExistingFundsLabel.setVisibility(View.VISIBLE);
                    binding.layoutExistingFunds.setVisibility(View.VISIBLE);

                    setFutureValue();

                    double futureValue = parseIdNumber(binding.tvExpenseYearlyRetirement.getText().toString().trim());
                    double fourPercentRule = calculateFourPercentRule(futureValue);
                    String fourPercentRuleFormat = numberFormat.format(fourPercentRule);
                    binding.tvTotalNeeded.setText(fourPercentRuleFormat);


                } else {
                    binding.tvExpenseYearlyRetirementLabel.setVisibility(View.GONE);
                    binding.layoutExpenseYearlyRetirement.setVisibility(View.GONE);
                    binding.layoutTotalNeeded.setVisibility(View.GONE);
                    binding.tvTotalNeededLabel.setVisibility(View.GONE);
                    binding.tvExistingFundsLabel.setVisibility(View.GONE);
                    binding.layoutExistingFunds.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etExistingFunds.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputStr = s.toString().trim();

                if (inputStr.isEmpty()) {
                    binding.tvMonthlyInvestmentNeededLabel.setVisibility(View.GONE);
                    binding.layoutMonthlyInvestmentNeeded.setVisibility(View.GONE);
                    return;
                } else if (parseIdNumber(inputStr) == 0.0) {
                    binding.etExistingFunds.setError("Angka harus lebih dari 0");
                    return;
                }

                binding.tvMonthlyInvestmentNeededLabel.setVisibility(View.VISIBLE);
                binding.layoutMonthlyInvestmentNeeded.setVisibility(View.VISIBLE);
            }

            @Override public void afterTextChanged(Editable s) {
                if (isBulkProgrammaticChange) return;
                if (!s.toString().equals(current)) {
                    binding.etExistingFunds.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d.,]", "");
                    if (cleanString.isEmpty()) {
                        current = "";
                        binding.etExistingFunds.setText("");
                        binding.etExistingFunds.addTextChangedListener(this);
                        return;
                    }
                    double parsed = parseIdNumber(cleanString);

                    String formatted = numberFormat.format(parsed);

                    current = formatted;
                    binding.etExistingFunds.setText(formatted);
                    binding.etExistingFunds.setSelection(formatted.length());

                    binding.etExistingFunds.addTextChangedListener(this);
                }
            }
        });

        binding.etMonthlyInvestment.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputStr = s.toString().trim();

                if (inputStr.isEmpty()) {
                    binding.tvInvestmentReturnLabel.setVisibility(View.GONE);
                    binding.layoutInvestmentReturn.setVisibility(View.GONE);
                    return;
                } else if (parseIdNumber(inputStr) == 0.0) {
                    binding.etMonthlyInvestment.setError("Angka harus lebih dari 0");
                    return;
                }

                binding.tvInvestmentReturnLabel.setVisibility(View.VISIBLE);
                binding.layoutInvestmentReturn.setVisibility(View.VISIBLE);
            }

            @Override public void afterTextChanged(Editable s) {
                if (isBulkProgrammaticChange) return;
                if (!s.toString().equals(current)) {
                    binding.etMonthlyInvestment.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d.,]", "");
                    if (cleanString.isEmpty()) {
                        current = "";
                        binding.etMonthlyInvestment.setText("");
                        binding.etMonthlyInvestment.addTextChangedListener(this);
                        return;
                    }
                    double parsed = parseIdNumber(cleanString);

                    String formatted = numberFormat.format(parsed);

                    current = formatted;
                    binding.etMonthlyInvestment.setText(formatted);
                    binding.etMonthlyInvestment.setSelection(formatted.length());

                    binding.etMonthlyInvestment.addTextChangedListener(this);
                }
            }
        });

        binding.etInvestmentReturn.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputStr = s.toString().trim();

                if (inputStr.isEmpty()) {
                    binding.tvAgeNeededLabel.setVisibility(View.GONE);
                    binding.layoutAgeNeeded.setVisibility(View.GONE);
                    binding.layoutCtaResult.setVisibility(View.GONE);
                    return;
                } else if (parseIdNumber(inputStr) == 0.0) {
                    binding.etInvestmentReturn.setError("Angka harus lebih dari 0");
                    return;
                }

                binding.tvAgeNeededLabel.setVisibility(View.VISIBLE);
                binding.layoutAgeNeeded.setVisibility(View.VISIBLE);

                String ageNowStr = binding.etAgeNow.getText().toString().trim();
                String ageRetirementStr = binding.etRetirementAge.getText().toString().trim();
                int ageNow = Integer.parseInt(ageNowStr);
                int ageRetirement = Integer.parseInt(ageRetirementStr);
                int years = ageRetirement - ageNow;

                binding.tvAgeNeeded.setText(String.valueOf(years) + " tahun");

                binding.layoutCtaResult.setVisibility(View.VISIBLE);

            }

            @Override public void afterTextChanged(Editable s) {
                if (isBulkProgrammaticChange) return;
                if (!s.toString().equals(current)) {
                    binding.etInvestmentReturn.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d.,]", "");
                    if (cleanString.isEmpty()) {
                        current = "";
                        binding.etInvestmentReturn.setText("");
                        binding.etInvestmentReturn.addTextChangedListener(this);
                        return;
                    }
                    double parsed = parseIdNumber(cleanString);

                    String formatted = numberFormat.format(parsed);

                    current = formatted;
                    binding.etInvestmentReturn.setText(formatted);
                    binding.etInvestmentReturn.setSelection(formatted.length());

                    binding.etInvestmentReturn.addTextChangedListener(this);
                }
            }
        });

        binding.cardCtaArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil semua input di dalam onClick
                String ageNowStr = binding.etAgeNow.getText().toString().trim();
                String ageRetirementStr = binding.etRetirementAge.getText().toString().trim();
                int ageNow = 0;
                int ageRetirement = 0;
                if (!ageNowStr.isEmpty()) {
                    ageNow = Integer.parseInt(ageNowStr);
                }
                if (!ageRetirementStr.isEmpty()) {
                    ageRetirement = Integer.parseInt(ageRetirementStr);
                }
                int years = ageRetirement - ageNow;

                String totalNeeded = binding.tvTotalNeeded.getText().toString().trim();
                String existingFunds = binding.etExistingFunds.getText().toString().trim();
                String monthlyInvestment = binding.etMonthlyInvestment.getText().toString().trim();
                String returnRate = binding.etInvestmentReturn.getText().toString().trim();

                Bundle bundle = new Bundle();
                bundle.putString("total_needed", totalNeeded);
                bundle.putString("existing_fund", existingFunds);
                bundle.putString("monthly_investment", monthlyInvestment);
                bundle.putString("return_rate", returnRate);
                bundle.putString("duration", String.valueOf(years));
                bundle.putString("tahun_target", ageRetirementStr);

                ResultFragment resultFragment = new ResultFragment();
                resultFragment.setArguments(bundle);

                // Ganti R.id.fragment_container dengan id container fragment kamu
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, resultFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        //region Future value
//        double futureValue =
        //endregion

        return view;
    }

    public void setFutureValue() {
        //region NumberFormat
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        DecimalFormatSymbols symbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(symbols);
        //endregion

        String ageNowStr = binding.etAgeNow.getText().toString().trim();
        String ageRetirementStr = binding.etRetirementAge.getText().toString().trim();
        String expenseYearlyStr = binding.tvExpenseYearly.getText().toString().trim();
        String inflationRateStr = binding.etInflationRate.getText().toString().trim();

        if (ageNowStr.isEmpty() || ageRetirementStr.isEmpty() || expenseYearlyStr.isEmpty() || inflationRateStr.isEmpty())
            return;

        int ageNow = Integer.parseInt(ageNowStr);
        int ageRetirement = Integer.parseInt(ageRetirementStr);
        double presentValue = parseIdNumber(expenseYearlyStr);
        double inflationRate = Double.parseDouble(inflationRateStr);
        int years = ageRetirement - ageNow;

        double rate = inflationRate / 100.0;
        double result = presentValue * Math.pow(1 + rate, years);

        String formattedResult = numberFormat.format(result);

        binding.tvExpenseYearlyRetirement.setText(formattedResult);
    }

    public double calculateFourPercentRule(double yearlyExpense) {
        return yearlyExpense * 25;
    }

    public double parseIdNumber(String s) {
        if (s == null || s.trim().isEmpty()) return 0;
        // Hilangkan titik ribuan, ganti koma menjadi titik desimal
        s = s.replace(".", "").replace(",", ".");
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}