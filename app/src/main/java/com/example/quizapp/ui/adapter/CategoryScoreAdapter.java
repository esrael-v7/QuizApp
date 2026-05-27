package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.CategoryScore;
import com.example.quizapp.databinding.ItemCategoryScoreBinding;
import java.util.ArrayList;
import java.util.List;

public class CategoryScoreAdapter extends RecyclerView.Adapter<CategoryScoreAdapter.ViewHolder> {
    private List<CategoryScore> items = new ArrayList<>();

    public void submitList(List<CategoryScore> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCategoryScoreBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryScoreBinding binding;

        ViewHolder(ItemCategoryScoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CategoryScore item) {
            binding.tvCategoryName.setText(item.category_name);
            binding.pbScore.setProgress(item.score);
            binding.tvScoreValue.setText(item.score + "%");
        }
    }
}
