package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.RadioButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quizapp.data.remote.model.AnswerRecord;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.ActivityQuizBinding;
import com.example.quizapp.ui.viewmodel.QuizViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private QuizViewModel viewModel;
    private CountDownTimer timer;
    private List<Question> questionsList = new ArrayList<>();
    private List<AnswerRecord> answersList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int categoryId;
    private String categoryName;
    private int correctCount = 0;
    private boolean isAnswerRevealed = false;
    private boolean isPracticeMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        categoryId = getIntent().getIntExtra("category_id", -1);
        categoryName = getIntent().getStringExtra("category_name");
        String practiceQuestionsJson = getIntent().getStringExtra("questions_json");
        isPracticeMode = practiceQuestionsJson != null;

        binding.toolbar.setTitle(categoryName != null ? categoryName : "Practice");
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        observeViewModel();

        if (isPracticeMode) {
            try {
                // Practice mode: use provided questions
                List<Question> practiceQuestions = new Gson().fromJson(practiceQuestionsJson, new TypeToken<List<Question>>(){}.getType());
                if (practiceQuestions != null && !practiceQuestions.isEmpty()) {
                    questionsList = practiceQuestions;
                    showQuestion(0);
                    startQuizTimer();
                } else {
                    Snackbar.make(binding.getRoot(), "Failed to load practice questions", Snackbar.LENGTH_LONG).show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            // Normal mode: fetch from server
            viewModel.loadQuestions(categoryId);
        }

        binding.toolbar.inflateMenu(R.menu.quiz_menu);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_favourite) {
                saveToFavourites();
                return true;
            }
            return false;
        });

        // When user selects an answer
        binding.rbA.setOnClickListener(v -> onOptionSelected());
        binding.rbB.setOnClickListener(v -> onOptionSelected());
        binding.rbC.setOnClickListener(v -> onOptionSelected());
        binding.rbD.setOnClickListener(v -> onOptionSelected());

        binding.btnNext.setOnClickListener(v -> {
            showQuestion(currentQuestionIndex + 1);
        });
    }

    private void onOptionSelected() {
        if (!isAnswerRevealed) {
            handleCheckAnswer();
            binding.btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void saveToFavourites() {
        if (currentQuestionIndex < questionsList.size()) {
            Question currentQuestion = questionsList.get(currentQuestionIndex);
            viewModel.addFavourite(currentQuestion.id);
            Snackbar.make(binding.getRoot(), "Question added to favourites", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void observeViewModel() {
        viewModel.getQuestions().observe(this, questions -> {
            if (questions != null && !questions.isEmpty()) {
                questionsList = questions;
                showQuestion(0);
                startQuizTimer();
            }
        });

        viewModel.getError().observe(this, error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }

    private void showQuestion(int index) {
        if (index >= questionsList.size()) {
            finishQuiz();
            return;
        }

        currentQuestionIndex = index;
        isAnswerRevealed = false;
        Question question = questionsList.get(index);

        binding.tvProgress.setText("Question " + (index + 1) + " of " + questionsList.size());
        binding.progressBar.setProgress((index * 100) / questionsList.size());
        
        binding.tvQuestion.setText(question.question_text != null ? question.question_text : "Question text missing");

        binding.rbA.setText(question.option_a != null ? question.option_a : "Option A");
        binding.rbA.setTag("a");
        binding.rbB.setText(question.option_b != null ? question.option_b : "Option B");
        binding.rbB.setTag("b");
        binding.rbC.setText(question.option_c != null ? question.option_c : "Option C");
        binding.rbC.setTag("c");
        binding.rbD.setText(question.option_d != null ? question.option_d : "Option D");
        binding.rbD.setTag("d");

        resetOptions();
    }

    private void resetOptions() {
        binding.rgOptions.clearCheck();
        binding.rbA.setEnabled(true);
        binding.rbB.setEnabled(true);
        binding.rbC.setEnabled(true);
        binding.rbD.setEnabled(true);
        binding.rbA.setActivated(false);
        binding.rbB.setActivated(false);
        binding.rbC.setActivated(false);
        binding.rbD.setActivated(false);
        binding.rbA.setSelected(false);
        binding.rbB.setSelected(false);
        binding.rbC.setSelected(false);
        binding.rbD.setSelected(false);
        
        // Reset text color to theme default
        int defaultColor = getResources().getColor(R.color.colorPrimaryText);
        binding.rbA.setTextColor(defaultColor);
        binding.rbB.setTextColor(defaultColor);
        binding.rbC.setTextColor(defaultColor);
        binding.rbD.setTextColor(defaultColor);

        binding.tvExplanation.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.GONE);
        binding.btnNext.setText("Check answer");
    }

    private void startQuizTimer() {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(40000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                binding.tvTimer.setText(String.format("00:%02d", seconds));
                if (seconds <= 10) {
                    binding.tvTimer.setTextColor(getResources().getColor(R.color.colorError));
                } else {
                    binding.tvTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                showTimesUpDialog();
            }
        }.start();
    }

    private void showTimesUpDialog() {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("Time's Up!")
                .setMessage("Sorry, your time has expired. Please try again!")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> finish())
                .show();
    }

    private void handleCheckAnswer() {
        try {
            isAnswerRevealed = true;

            int checkedId = binding.rgOptions.getCheckedRadioButtonId();
            String selectedOption = "";
            if (checkedId != -1) {
                RadioButton rb = findViewById(checkedId);
                selectedOption = rb.getTag() != null ? rb.getTag().toString() : "";
            }

            Question question = questionsList.get(currentQuestionIndex);
            
            // Safe null check for correct_option
            String correct = question.correct_option != null ? question.correct_option : "";
            
            boolean isCorrect = selectedOption.equalsIgnoreCase(correct);
            if (isCorrect) correctCount++;

            revealAnswer(correct, selectedOption);

            if (question.explanation != null && !question.explanation.isEmpty()) {
                binding.tvExplanation.setText(question.explanation);
                binding.tvExplanation.setVisibility(View.VISIBLE);
            } else {
                binding.tvExplanation.setVisibility(View.GONE);
            }

            answersList.add(new AnswerRecord(question.id, selectedOption, isCorrect, 0));

            if (currentQuestionIndex == questionsList.size() - 1) {
                binding.btnNext.setText("Finish quiz");
            } else {
                binding.btnNext.setText("Next question");
            }

            // Disable options after selection
            binding.rbA.setEnabled(false);
            binding.rbB.setEnabled(false);
            binding.rbC.setEnabled(false);
            binding.rbD.setEnabled(false);
            
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(binding.getRoot(), "Something went wrong. Please try again.", Snackbar.LENGTH_LONG).show();
            finish();
        }
    }

    private void revealAnswer(String correctOption, String selectedOption) {
        if (correctOption == null) return;
        
        // In Light Mode, text should be Black on Light Green/Red.
        // In Dark Mode, text should be White on Dark Green/Red.
        // Using textColorPrimary ensures the theme's default text color is used.
        int textColor = getResources().getColor(R.color.colorPrimaryText); 

        // Match the green "Activated" state to the correct option
        if (correctOption.equalsIgnoreCase("a")) {
            binding.rbA.setActivated(true);
            binding.rbA.setTextColor(textColor);
        }
        if (correctOption.equalsIgnoreCase("b")) {
            binding.rbB.setActivated(true);
            binding.rbB.setTextColor(textColor);
        }
        if (correctOption.equalsIgnoreCase("c")) {
            binding.rbC.setActivated(true);
            binding.rbC.setTextColor(textColor);
        }
        if (correctOption.equalsIgnoreCase("d")) {
            binding.rbD.setActivated(true);
            binding.rbD.setTextColor(textColor);
        }

        // If wrong answer selected, match the red "Selected" state
        if (!selectedOption.equalsIgnoreCase(correctOption)) {
            if (selectedOption.equalsIgnoreCase("a")) {
                binding.rbA.setSelected(true);
                binding.rbA.setTextColor(textColor);
            }
            if (selectedOption.equalsIgnoreCase("b")) {
                binding.rbB.setSelected(true);
                binding.rbB.setTextColor(textColor);
            }
            if (selectedOption.equalsIgnoreCase("c")) {
                binding.rbC.setSelected(true);
                binding.rbC.setTextColor(textColor);
            }
            if (selectedOption.equalsIgnoreCase("d")) {
                binding.rbD.setSelected(true);
                binding.rbD.setTextColor(textColor);
            }
        }
    }

    private void finishQuiz() {
        if (timer != null) timer.cancel();
        
        int totalTime = 40; // Total allowed time
        // Calculate remaining seconds if we wanted to be precise, but for now 40 is max.
        
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("correct", correctCount);
        intent.putExtra("total", questionsList.size());
        intent.putExtra("category_id", categoryId);
        intent.putExtra("category_name", categoryName);
        intent.putExtra("time_taken_secs", totalTime);
        intent.putExtra("is_practice", isPracticeMode);
        intent.putExtra("answers_json", new Gson().toJson(answersList));
        intent.putExtra("questions_json", new Gson().toJson(questionsList));
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit quiz?")
                .setMessage("Progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
