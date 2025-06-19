package com.example.masatua;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.masatua.databinding.FragmentGoalsListBinding;
import com.example.masatua.utils.FragmentUtils;

public class GoalsListFragment extends Fragment {
    private FragmentGoalsListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Setup binding
        binding = FragmentGoalsListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // region Button add logic
        binding.fabAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentUtils.addFragment(
                        getParentFragmentManager(),
                        new CreateGoalFragment(),
                        R.id.fragment_container,
                        "GoalsListFragmentTag",
                        true
                );
            }
        });
        // endregion

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}