package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.ItemFavouriteBinding;

public class FavouriteAdapter extends ListAdapter<Question, FavouriteAdapter.ViewHolder> {

    public interface OnFavouriteClickListener {
        void onRemoveClick(Question question);
        void onPracticeClick(Question question);
    }

    private final OnFavouriteClickListener listener;

    public FavouriteAdapter(OnFavouriteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<Question> DIFF_CALLBACK = new DiffUtil.ItemCallback<Question>() {
        @Override
        public boolean areItemsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
            return oldItem.question_text.equals(newItem.question_text);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavouriteBinding binding = ItemFavouriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavouriteBinding binding;

        public ViewHolder(ItemFavouriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Question question, OnFavouriteClickListener listener) {
            binding.tvQuestion.setText(question.question_text);
            binding.tvMeta.setText(question.difficulty); // Category name missing in Question model
            
            binding.btnRemove.setOnClickListener(v -> listener.onRemoveClick(question));
            binding.ibStar.setOnClickListener(v -> listener.onRemoveClick(question));
            binding.btnPractice.setOnClickListener(v -> listener.onPracticeClick(question));
        }
    }
}
