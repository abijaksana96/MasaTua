package com.example.masatua.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.masatua.databinding.ItemGoalBinding;
import com.example.masatua.models.Goal;

import com.example.masatua.utils.FirebaseManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

public class GoalAdapter extends FirestoreRecyclerAdapter<Goal, GoalAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot snapshot, Goal goal);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(DocumentSnapshot snapshot, Goal goal);
    }

    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;

    public GoalAdapter(@NonNull FirestoreRecyclerOptions<Goal> options, OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        super(options);
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Goal model) {
        final DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);

        holder.bind(model);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(snapshot, model);
            }
        });

        holder.binding.ivDeleteIcon.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(snapshot, model);
            }
        });

        // Pass the snapshot id to ViewHolder for update
        holder.binding.btnSendInvestment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.updateDanaSekarang(snapshot.getId());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemGoalBinding binding;

        public ViewHolder(ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Goal goal) {
            binding.tvGoalTitle.setText(goal.getNamaGoals());

            StringBuilder fundDescription = new StringBuilder();
            fundDescription.append("Dana Saat Ini : Rp");
            fundDescription.append(goal.getDanaSekarang());
            fundDescription.append(" Dari Target Rp");
            fundDescription.append(goal.getTargetDana());

            binding.tvFundDescription.setText(fundDescription);

            int percent = goal.getProgres();
            if (percent > 0) {
                binding.progressBar.setProgress(percent);
                binding.tvProgressPercentage.setText(percent + "%");
            } else {
                binding.progressBar.setProgress(0);
                binding.tvProgressPercentage.setText("0%");
            }
        }

        // Pindahkan updateDanaSekarang ke dalam ViewHolder
        public void updateDanaSekarang(String goalId) {
            String investmentStr = binding.etAddInvestment.getText().toString().trim();
            if (!TextUtils.isEmpty(investmentStr)) {
                try {
                    long investment = Long.parseLong(investmentStr);
                    FirebaseManager.getInstance().getUserGoalsCollection().document(goalId)
                            .update("danaSekarang", FieldValue.increment(investment))
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(binding.getRoot().getContext(), "Dana berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                binding.etAddInvestment.setText(""); // CLEAR THE FIELD!
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(binding.getRoot().getContext(), "Gagal memperbarui dana", Toast.LENGTH_SHORT).show();
                            });
                } catch (NumberFormatException e) {
                    Toast.makeText(binding.getRoot().getContext(), "Input investasi tidak valid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(binding.getRoot().getContext(), "Masukkan jumlah investasi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}