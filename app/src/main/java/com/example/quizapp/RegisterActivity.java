package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.quizapp.databinding.ActivityRegisterBinding;
import com.example.quizapp.ui.viewmodel.RegisterViewModel;
import com.example.quizapp.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private RegisterViewModel viewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);


        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInput(fullName, email, password)) {
                viewModel.register(fullName, email, password);
            }
        });
    }

    private boolean validateInput(String fullName, String email, String password) {
        boolean isValid = true;
        if (fullName.isEmpty()) {
            binding.tilFullName.setError("Full name is required");
            isValid = false;
        } else {
            binding.tilFullName.setError(null);
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }
        return isValid;
    }

    private void observeViewModel() {
        viewModel.getRegisterResult().observe(this, response -> {
            if (response != null && response.data != null) {
                android.widget.Toast.makeText(this, "Account created successfully! Welcome " + response.data.username, android.widget.Toast.LENGTH_LONG).show();
                tokenManager.saveTokens(
                        response.data.token,
                        null,
                        response.data.id,
                        response.data.role,
                        response.data.username
                );
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity(); // Clear backstack
            }
        });

        viewModel.getError().observe(this, errorMessage -> {
            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
        });

        viewModel.isLoading().observe(this, isLoading -> {
            binding.btnRegister.setEnabled(!isLoading);
            binding.btnRegister.setText(isLoading ? "Creating account..." : getString(R.string.create_account));
        });
    }
}
