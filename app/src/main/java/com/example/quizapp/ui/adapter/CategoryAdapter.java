package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.databinding.ItemCategoryBinding;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private final OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.name.equals(newItem.name) && oldItem.question_count == newItem.question_count;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public ViewHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category, OnCategoryClickListener listener) {
            binding.tvName.setText(category.name);
            binding.tvCount.setText(category.question_count + " questions");
            
            int bgColor, iconTint, iconRes;
            String name = category.name.toLowerCase();
            
            if (name.contains("java")) {
                bgColor = 0xFFFFF3E0; iconTint = 0xFFE65100; iconRes = com.example.quizapp.R.drawable.ic_star;
            } else if (name.contains("python")) {
                bgColor = 0xFFE3F2FD; iconTint = 0xFF1565C0; iconRes = com.example.quizapp.R.drawable.ic_trophy;
            } else if (name.contains("web")) {
                bgColor = 0xFFF3E5F5; iconTint = 0xFF7B1FA2; iconRes = com.example.quizapp.R.drawable.ic_home;
            } else if (name.contains("database")) {
                bgColor = 0xFFFFEBEE; iconTint = 0xFFC62828; iconRes = com.example.quizapp.R.drawable.ic_chart_bar;
            } else if (name.contains("general")) {
                bgColor = 0xFFE8F5E9; iconTint = 0xFF2E7D32; iconRes = com.example.quizapp.R.drawable.ic_star;
            } else if (name.contains("movie")) {
                bgColor = 0xFFFFFDE7; iconTint = 0xFFFBC02D; iconRes = com.example.quizapp.R.drawable.ic_trophy;
            } else if (name.contains("music")) {
                bgColor = 0xFFE0F7FA; iconTint = 0xFF00838F; iconRes = com.example.quizapp.R.drawable.ic_home;
            } else if (name.contains("history")) {
                bgColor = 0xFFEFEBE9; iconTint = 0xFF4E342E; iconRes = com.example.quizapp.R.drawable.ic_chart_bar;
            } else {
                bgColor = 0xFFF5F5F5; iconTint = 0xFF616161; iconRes = com.example.quizapp.R.drawable.ic_star;
            }
            
            binding.getRoot().setCardBackgroundColor(bgColor);
            binding.ivIcon.setImageResource(iconRes);
            binding.ivIcon.setColorFilter(iconTint);
            binding.tvName.setTextColor(iconTint);
            binding.tvCount.setTextColor(iconTint);
            
            itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        }
    }
}
