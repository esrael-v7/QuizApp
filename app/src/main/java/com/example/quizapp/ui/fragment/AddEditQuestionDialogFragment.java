package com.example.quizapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.DialogAddEditQuestionBinding;

public class AddEditQuestionDialogFragment extends DialogFragment {

    public interface OnQuestionSavedListener {
        void onSave(Question question);
    }

    private DialogAddEditQuestionBinding binding;
    private OnQuestionSavedListener listener;
    private Question existingQuestion;

    public static AddEditQuestionDialogFragment newInstance(Question question) {
        AddEditQuestionDialogFragment fragment = new AddEditQuestionDialogFragment();
        fragment.existingQuestion = question;
        return fragment;
    }

    public void setOnQuestionSavedListener(OnQuestionSavedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_MaterialComponents_DayNight_Dialog_MinWidth);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAddEditQuestionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDropdown();

        if (existingQuestion != null) {
            binding.tvTitle.setText("Edit Question");
            binding.etQuestionText.setText(existingQuestion.question_text);
            binding.etCategoryId.setText(String.valueOf(existingQuestion.category_id));
            binding.etOptionA.setText(existingQuestion.option_a);
            binding.etOptionB.setText(existingQuestion.option_b);
            binding.etOptionC.setText(existingQuestion.option_c);
            binding.etOptionD.setText(existingQuestion.option_d);
            binding.actvCorrectOption.setText(existingQuestion.correct_option, false);
            binding.etExplanation.setText(existingQuestion.explanation);
        }

        binding.btnSave.setOnClickListener(v -> {
            if (validate()) {
                Question q = existingQuestion != null ? existingQuestion : new Question();
                if (existingQuestion == null) q.id = 0; // Explicitly 0 for new
                q.question_text = binding.etQuestionText.getText().toString().trim();

                q.category_id = Integer.parseInt(binding.etCategoryId.getText().toString().trim());
                q.option_a = binding.etOptionA.getText().toString().trim();
                q.option_b = binding.etOptionB.getText().toString().trim();
                q.option_c = binding.etOptionC.getText().toString().trim();
                q.option_d = binding.etOptionD.getText().toString().trim();
                // Use UPPERCASE to satisfy database check constraints
                q.correct_option = binding.actvCorrectOption.getText().toString().toUpperCase().trim();
                q.explanation = binding.etExplanation.getText().toString().trim();


                q.difficulty = "Medium"; // capitalized default

                if (listener != null) {

                    listener.onSave(q);
                }
                dismiss();
            }
        });
    }

    private void setupDropdown() {
        String[] options = {"A", "B", "C", "D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, options);
        binding.actvCorrectOption.setAdapter(adapter);
    }


    private boolean validate() {
        if (binding.etQuestionText.getText().toString().trim().isEmpty()) {
            binding.etQuestionText.setError("Required");
            return false;
        }
        String catIdStr = binding.etCategoryId.getText().toString().trim();
        if (catIdStr.isEmpty()) {
            binding.etCategoryId.setError("Required");
            return false;
        }
        try {
            Integer.parseInt(catIdStr);
        } catch (NumberFormatException e) {
            binding.etCategoryId.setError("Must be a number");
            return false;
        }
        if (binding.etOptionA.getText().toString().trim().isEmpty()) {
            binding.etOptionA.setError("Required");
            return false;
        }
        if (binding.etOptionB.getText().toString().trim().isEmpty()) {
            binding.etOptionB.setError("Required");
            return false;
        }
        if (binding.etOptionC.getText().toString().trim().isEmpty()) {
            binding.etOptionC.setError("Required");
            return false;
        }
        if (binding.etOptionD.getText().toString().trim().isEmpty()) {
            binding.etOptionD.setError("Required");
            return false;
        }
        if (binding.actvCorrectOption.getText().toString().trim().isEmpty()) {
            binding.actvCorrectOption.setError("Required");
            return false;
        }
        if (binding.etExplanation.getText().toString().trim().isEmpty()) {
            binding.etExplanation.setError("Required");
            return false;
        }
        return true;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
