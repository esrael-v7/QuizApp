package com.example.quizapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.data.remote.model.AnswerRecord;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.ActivityReviewBinding;
import com.example.quizapp.databinding.ItemReviewBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private ActivityReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityReviewBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

            String questionsJson = getIntent().getStringExtra("questions_json");
            String answersJson = getIntent().getStringExtra("answers_json");

            if (questionsJson == null || answersJson == null) {
                showErrorAndExit();
                return;
            }

            List<Question> questions = new Gson().fromJson(questionsJson, new TypeToken<List<Question>>(){}.getType());
            List<AnswerRecord> answers = new Gson().fromJson(answersJson, new TypeToken<List<AnswerRecord>>(){}.getType());

            if (questions == null || questions.isEmpty() || answers == null) {
                binding.tvEmpty.setVisibility(android.view.View.VISIBLE);
                binding.rvReview.setVisibility(android.view.View.GONE);
            } else {
                binding.tvEmpty.setVisibility(android.view.View.GONE);
                binding.rvReview.setVisibility(android.view.View.VISIBLE);
                setupRecyclerView(questions, answers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAndExit();
        }
    }

    private void showErrorAndExit() {
        com.google.android.material.snackbar.Snackbar.make(findViewById(android.R.id.content), 
                "Failed to load review data", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
        new android.os.Handler().postDelayed(this::finish, 2000);
    }

    private void setupRecyclerView(List<Question> questions, List<AnswerRecord> answers) {
        binding.rvReview.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReview.setAdapter(new ReviewAdapter(questions, answers));
    }

    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
        private final List<Question> questions;
        private final List<AnswerRecord> answers;

        ReviewAdapter(List<Question> questions, List<AnswerRecord> answers) {
            this.questions = questions;
            this.answers = answers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemReviewBinding binding = ItemReviewBinding.inflate(android.view.LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < questions.size() && position < answers.size()) {
                Question q = questions.get(position);
                AnswerRecord a = answers.get(position);
                holder.bind(q, a);
            }
        }

        @Override
        public int getItemCount() {
            return questions != null ? Math.min(questions.size(), answers.size()) : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemReviewBinding binding;

            ViewHolder(ItemReviewBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(Question q, AnswerRecord a) {
                binding.tvQuestion.setText(q.question_text);
                
                String userOptionText = getOptionText(q, a.selected_option);
                String correctOptionText = getOptionText(q, q.correct_option);

                if (a.is_correct) {
                    binding.tvUserAnswer.setText("Your answer: " + userOptionText + " (Correct)");
                    binding.tvUserAnswer.setTextColor(itemView.getContext().getResources().getColor(R.color.colorSuccess));
                    binding.tvCorrectAnswer.setVisibility(android.view.View.GONE);
                } else {
                    binding.tvUserAnswer.setText("Your answer: " + userOptionText);
                    binding.tvUserAnswer.setTextColor(itemView.getContext().getResources().getColor(R.color.colorError));
                    
                    binding.tvCorrectAnswer.setText("Correct answer: " + correctOptionText);
                    binding.tvCorrectAnswer.setTextColor(itemView.getContext().getResources().getColor(R.color.colorSuccess));
                    binding.tvCorrectAnswer.setVisibility(android.view.View.VISIBLE);
                }

                if (q.explanation != null && !q.explanation.isEmpty()) {
                    binding.tvExplanation.setText(q.explanation);
                    binding.tvExplanation.setVisibility(android.view.View.VISIBLE);
                } else {
                    binding.tvExplanation.setVisibility(android.view.View.GONE);
                }
            }

            private String getOptionText(Question q, String option) {
                if (option == null) return "No answer";
                switch (option.toLowerCase()) {
                    case "a": return q.option_a;
                    case "b": return q.option_b;
                    case "c": return q.option_c;
                    case "d": return q.option_d;
                    default: return "No answer";
                }
            }
        }
    }
}
