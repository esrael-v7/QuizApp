package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quizapp.data.remote.model.AnswerRecord;
import com.example.quizapp.data.remote.model.SubmitResultRequest;
import com.example.quizapp.databinding.ActivityResultsBinding;
import com.example.quizapp.ui.viewmodel.ResultsViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private ActivityResultsBinding binding;
    private ResultsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ResultsViewModel.class);

        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);
        int categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");
        int timeTaken = getIntent().getIntExtra("time_taken_secs", 0);
        String answersJson = getIntent().getStringExtra("answers_json");
        boolean isPractice = getIntent().getBooleanExtra("is_practice", false);

        double scorePercent = (correct / (double) total) * 100;
        boolean passed = scorePercent >= 60;
        int points = (int) (correct * 10 * (passed ? 1.5 : 1.0));

        displayResults(correct, total, categoryName, timeTaken, scorePercent, points, passed);
        setupListeners();
        observeViewModel();

        // Auto-submit if not in practice mode
        if (answersJson != null && !isPractice) {
            try {
                List<AnswerRecord> answers = new Gson().fromJson(answersJson, new TypeToken<List<AnswerRecord>>(){}.getType());
                SubmitResultRequest request = new SubmitResultRequest(categoryId, total, correct, timeTaken, answers);
                viewModel.submitResult(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void displayResults(int correct, int total, String categoryName, int timeTaken, double scorePercent, int points, boolean passed) {
        binding.tvScorePercent.setText(String.format(Locale.getDefault(), "%.0f%%", scorePercent));
        binding.scoreRing.setProgress((int) scorePercent);
        binding.scoreRing.setIndicatorColor(getResources().getColor(passed ? R.color.colorSuccess : R.color.colorError));

        binding.tvResult.setText(passed ? "Passed!" : "Keep practising!");
        binding.tvResult.setTextColor(getResources().getColor(passed ? R.color.colorSuccess : R.color.colorError));
        binding.tvSummary.setText(String.format(Locale.getDefault(), "%s · %d/%d correct", categoryName, correct, total));
        
        binding.tvCorrectCount.setText(String.valueOf(correct));
        binding.tvWrongCount.setText(String.valueOf(total - correct));
        
        int minutes = timeTaken / 60;
        int seconds = timeTaken % 60;
        binding.tvTimeTaken.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        binding.tvPoints.setText(String.format(Locale.getDefault(), "+%d pts", points));
    }

    private void setupListeners() {
        binding.btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        binding.btnShare.setOnClickListener(v -> {
            String shareText = String.format("I scored %.0f%% on QuizMaster! (%d/%d) #StMarysQuiz", 
                (getIntent().getIntExtra("correct", 0) / (double) getIntent().getIntExtra("total", 0)) * 100,
                getIntent().getIntExtra("correct", 0), getIntent().getIntExtra("total", 0));
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(intent, "Share result"));
        });
        
        binding.btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("questions_json", getIntent().getStringExtra("questions_json"));
            intent.putExtra("answers_json", getIntent().getStringExtra("answers_json"));
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        viewModel.getError().observe(this, error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }
}
