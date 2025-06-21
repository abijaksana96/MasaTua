package com.example.masatua;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masatua.adapters.GoalAdapter;
import com.example.masatua.databinding.FragmentGoalsListBinding;
import com.example.masatua.models.Goal;
import com.example.masatua.utils.FirebaseManager;
import com.example.masatua.utils.FragmentUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class GoalsListFragment extends Fragment implements GoalAdapter.OnItemClickListener, GoalAdapter.OnDeleteClickListener{
    private FragmentGoalsListBinding binding;
    private FirebaseManager firebaseManager;
    private GoalAdapter goalAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = FirebaseManager.getInstance();
    }

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

        binding.rvGoalsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pastikan user sudah login sebelum setup adapter
        if (firebaseManager.isUserLoggedIn()) {
            setupFirestoreAdapter();
        } else {
            // Jika user belum login
            Toast.makeText(getContext(), "Silakan login untuk melihat daftar goals Anda.", Toast.LENGTH_LONG).show();
        }

    }

    private void setupFirestoreAdapter() {
        // Dapatkan referensi koleksi goal untuk user yang sedang login
        // Asumsi Anda memiliki subkoleksi 'goals' di dalam dokumen user di Firestore,
        // mirip dengan 'items' yang kita bahas sebelumnya.
        // Struktur Firestore: /users/{userId}/goals/{goalId}
        Query query = firebaseManager.getCurrentUserDocument().collection("goals");// Urutkan berdasarkan timestamp atau field lain

        // Konfigurasi FirestoreRecyclerOptions
        FirestoreRecyclerOptions<Goal> options =
                new FirestoreRecyclerOptions.Builder<Goal>()
                        .setQuery(query, Goal.class) // Goal.class harus punya konstruktor no-arg dan getter/setter
                        .build();

        // Inisialisasi GoalAdapter
        goalAdapter = new GoalAdapter(options, this, this); // 'this' karena fragment mengimplementasikan OnItemClickListener

        // Observer untuk menangani kasus data kosong
        goalAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
//                checkEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
//                checkEmptyState();
            }

            @Override
            public void onChanged() {
                super.onChanged();
//                checkEmptyState();
            }

//            private void checkEmptyState() {
//                if (goalAdapter.getItemCount() == 0) {
//                    binding.tvNoDataMessage.setVisibility(View.VISIBLE);
//                    binding.tvNoDataMessage.setText("Belum ada goals. Tambahkan goal baru!");
//                } else {
//                    binding.tvNoDataMessage.setVisibility(View.GONE);
//                }
//            }
        });

        binding.rvGoalsList.setAdapter(goalAdapter);
        goalAdapter.startListening(); // Mulai mendengarkan data
    }

    @Override
    public void onStart() {
        super.onStart();
        if (goalAdapter != null) {
            goalAdapter.startListening(); // Mulai mendengarkan data
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (goalAdapter != null) {
            goalAdapter.stopListening(); // Hentikan mendengarkan data
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (goalAdapter != null) {
            goalAdapter.stopListening(); // Pastikan listener dihentikan
        }
        binding = null;
    }

    // Implementasi metode dari GoalAdapter.OnItemClickListener
    @Override
    public void onItemClick(DocumentSnapshot snapshot, Goal goal) {
        // Di sini Anda bisa menangani klik pada item Goal
        // Misalnya, tampilkan detail goal atau navigasi ke layar edit.
        String goalId = snapshot.getId(); // Dapatkan ID dokumen goal

        // Buka fragment detail dan edit goal
        Bundle args = new Bundle();
        args.putString("goal_id", goalId);
        args.putSerializable("goal_object", goal);
        DetailEditGoalFragment detailFragment = new DetailEditGoalFragment();
        detailFragment.setArguments(args);
        getParentFragmentManager().beginTransaction()
                 .replace(R.id.fragment_container, detailFragment)
                 .addToBackStack(null)
                 .commit();
    }

    @Override
    public void onDeleteClick(DocumentSnapshot snapshot, Goal goal) {
        String goalId = snapshot.getId();
        String goalName = goal.getNamaGoals();

        // Tampilkan dialog konfirmasi hapus
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Target")
                .setMessage("Apakah Anda yakin ingin menghapus target '" + goalName + "'?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    deleteGoal(goalId);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteGoal(String goalId) {
        firebaseManager.getUserGoalsCollection().document(goalId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Target berhasil dihapus!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menghapus target: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}