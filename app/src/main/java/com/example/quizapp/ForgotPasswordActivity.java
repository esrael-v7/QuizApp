package com.example.quizapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quizapp.databinding.ActivityForgotPasswordBinding;
import com.example.quizapp.ui.viewmodel.ForgotPasswordViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private ForgotPasswordViewModel viewModel;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        setupListeners();
        observeViewModel();
    }

    private void setupListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnSendOtp.setOnClickListener(v -> {
            userEmail = binding.etEmail.getText().toString().trim();
            if (!userEmail.isEmpty()) {
                viewModel.sendOtp(userEmail);
            } else {
                binding.etEmail.setError("Email is required");
            }
        });

        binding.btnResetPassword.setOnClickListener(v -> {
            String otp = binding.etOtp.getText().toString().trim();
            String newPassword = binding.etNewPassword.getText().toString().trim();

            if (otp.isEmpty()) {
                binding.etOtp.setError("Required");
                return;
            }
            if (newPassword.length() < 6) {
                binding.etNewPassword.setError("Password must be at least 6 characters");
                return;
            }

            viewModel.resetPassword(userEmail, otp, newPassword);
        });
    }

    private void observeViewModel() {
        viewModel.isOtpSent().observe(this, sent -> {
            if (sent) {
                binding.layoutEmail.setVisibility(View.GONE);
                binding.layoutReset.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Reset code sent to your email", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isPasswordReset().observe(this, reset -> {
            if (reset) {
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.isLoading().observe(this, loading -> {
            binding.loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
            binding.btnSendOtp.setEnabled(!loading);
            binding.btnResetPassword.setEnabled(!loading);
        });
    }
}
