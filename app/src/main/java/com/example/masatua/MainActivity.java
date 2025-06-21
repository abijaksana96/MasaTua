package com.example.masatua;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.masatua.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;
    private Fragment activeFragment = null;
    private String activeFragmentTag = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // region Setup binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // endregion

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inisialisasi FragmentManager
        fragmentManager = getSupportFragmentManager();

        // region Set HomeFragment
        if (savedInstanceState == null) {
            loadTopLevelFragment(new HomeFragment(), "HomeFragment");
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
        // endregion

        // region Handler menu
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String tag = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                tag = "HomeFragment";
            } else if (item.getItemId() == R.id.nav_calculator) {
                selectedFragment = new CalculatorFragment();
                tag = "CalculatorFragment";
            } else if (item.getItemId() == R.id.nav_goals) {
                selectedFragment = new GoalsListFragment();
                tag = "GoalsListFragment";
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                tag = "ProfileFragment";
            }

            if (selectedFragment != null && !selectedFragment.getClass().equals(activeFragment.getClass())) {
                loadTopLevelFragment(selectedFragment, tag);
                return true;
            }
            return false;
        });
        // endregion
    }

    private void loadTopLevelFragment(Fragment fragment, String tag) {
        // Membersihkan semua fragment dari back stack
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Mengganti fragment di container
        fragmentTransaction.replace(R.id.fragment_container, fragment, tag);

        // Commit
        fragmentTransaction.commit();

        // Update active fragment
        activeFragment = fragment;
        activeFragmentTag = tag;
    }
}