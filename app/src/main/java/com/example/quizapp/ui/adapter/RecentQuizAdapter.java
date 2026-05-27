package com.example.quizapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.data.remote.model.QuizHistory;
import com.example.quizapp.databinding.ItemRecentQuizBinding;
import java.util.Locale;

public class RecentQuizAdapter extends ListAdapter<QuizHistory, RecentQuizAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(QuizHistory item);
    }

    private final OnItemClickListener listener;

    public RecentQuizAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<QuizHistory> DIFF_CALLBACK = new DiffUtil.ItemCallback<QuizHistory>() {
        @Override
        public boolean areItemsTheSame(@NonNull QuizHistory oldItem, @NonNull QuizHistory newItem) {
            return oldItem.session_id == newItem.session_id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuizHistory oldItem, @NonNull QuizHistory newItem) {
            return oldItem.score_percentage == newItem.score_percentage && 
                   oldItem.category_name.equals(newItem.category_name);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecentQuizBinding binding = ItemRecentQuizBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecentQuizBinding binding;

        public ViewHolder(ItemRecentQuizBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuizHistory item, OnItemClickListener listener) {
            binding.tvQuizName.setText(item.category_name);
            binding.tvDate.setText(item.completed_at);
            
            double displayPercentage = 0;
            if (item.score_percentage != null) {
                displayPercentage = item.score_percentage;
            } else if (item.total_questions > 0) {
                displayPercentage = (item.correct_answers / (double) item.total_questions) * 100;
            }
            
            binding.tvScore.setText(String.format(Locale.getDefault(), "%.0f%%", displayPercentage));

            boolean passed = displayPercentage >= 60;
            int bgColor = itemView.getContext().getResources().getColor(passed ? R.color.colorSuccessLight : R.color.colorErrorLight);
            int textColor = itemView.getContext().getResources().getColor(passed ? R.color.colorSuccessDark : R.color.colorErrorDark);
            
            binding.tvScore.getBackground().setTint(bgColor);
            binding.tvScore.setTextColor(textColor);

            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
