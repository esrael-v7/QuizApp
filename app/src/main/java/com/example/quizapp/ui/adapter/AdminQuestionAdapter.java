package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.ItemAdminQuestionBinding;

public class AdminQuestionAdapter extends ListAdapter<Question, AdminQuestionAdapter.ViewHolder> {

    public interface OnQuestionActionListener {
        void onEdit(Question question);
        void onDelete(Question question);
    }

    private final OnQuestionActionListener listener;

    public AdminQuestionAdapter(OnQuestionActionListener listener) {
        super(new DiffUtil.ItemCallback<Question>() {
            @Override
            public boolean areItemsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
                return oldItem.question_text.equals(newItem.question_text);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAdminQuestionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminQuestionBinding binding;

        ViewHolder(ItemAdminQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Question question) {
            binding.tvQuestion.setText(question.question_text);
            binding.tvCategory.setText("Category ID: " + question.category_id);
            binding.btnEdit.setOnClickListener(v -> listener.onEdit(question));
            binding.btnDelete.setOnClickListener(v -> listener.onDelete(question));
        }
    }
}
