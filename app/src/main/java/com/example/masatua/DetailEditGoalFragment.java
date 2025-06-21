package com.example.masatua;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masatua.databinding.FragmentDetailEditGoalBinding;
import com.example.masatua.models.Goal;

public class DetailEditGoalFragment extends Fragment {
    private String goalId;
    private Goal goal;
    FragmentDetailEditGoalBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup binding
        binding = FragmentDetailEditGoalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //region Get Arguments
        if (getArguments() != null) {
            goalId = getArguments().getString("goal_id");
            goal = (Goal) getArguments().getSerializable("goal_object");
        }
        //endregion

        tampilkanDetailGoal();

        //region Back button
        binding.ivBackButtonBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        //endregion

        return view;
    }

    private void tampilkanDetailGoal() {
        binding.etNamaGoals.setText(goal.getNamaGoals());
        binding.etDanaSaatIni.setText(String.valueOf(goal.getDanaSekarang()));
        binding.etTargetDana.setText(String.valueOf(goal.getTargetDana()));
        binding.etTahunTarget.setText(String.valueOf(goal.getTahunTarget()));
        binding.etInvest.setText(String.valueOf(goal.getInvest()));
        binding.etInvestReturn.setText(String.valueOf(goal.getReturnInvest()));
        binding.etDeskripsi.setText(goal.getDeskripsi());
        binding.progressBar.setProgress(goal.getProgres());
        binding.tvProgressPercentage.setText(String.valueOf(goal.getProgres()));
    }

}