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
                return oldItem.id == newItem.id &&
                       oldItem.category_id == newItem.category_id &&
                       (oldItem.question_text != null && oldItem.question_text.equals(newItem.question_text)) &&
                       (oldItem.correct_option != null && oldItem.correct_option.equals(newItem.correct_option)) &&
                       (oldItem.option_a != null && oldItem.option_a.equals(newItem.option_a)) &&
                       (oldItem.option_b != null && oldItem.option_b.equals(newItem.option_b)) &&
                       (oldItem.option_c != null && oldItem.option_c.equals(newItem.option_c)) &&
                       (oldItem.option_d != null && oldItem.option_d.equals(newItem.option_d)) &&
                       (oldItem.explanation != null && oldItem.explanation.equals(newItem.explanation));
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
            binding.tvQuestion.setText("#" + question.id + ": " + question.question_text);
            binding.tvCategory.setText("Category ID: " + question.category_id);
            binding.btnEdit.setOnClickListener(v -> listener.onEdit(question));

            binding.btnDelete.setOnClickListener(v -> listener.onDelete(question));
        }
    }
}
