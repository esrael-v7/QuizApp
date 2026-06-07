package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProvider;
import com.example.quizapp.data.remote.model.AuthData;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.databinding.ActivityAdminLoginBinding;
import com.example.quizapp.ui.viewmodel.LoginViewModel;
import com.example.quizapp.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;

public class AdminLoginActivity extends AppCompatActivity {


    private ActivityAdminLoginBinding binding;
    private LoginViewModel viewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);


        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                viewModel.login(email, password);
            }
        });

        observeViewModel();

        viewModel.isLoading().observe(this, isLoading -> {
            binding.btnLogin.setEnabled(!isLoading);
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

    }


    private void observeViewModel() {
        viewModel.getLoginResult().observe(this, response -> {
            if (response != null && response.data != null) {
                String role = response.data.role;
                Toast.makeText(this, "Login success! Role: " + role, Toast.LENGTH_SHORT).show();
                
                if (role != null && role.equalsIgnoreCase("admin")) {
                    tokenManager.saveTokens(
                            response.data.token,
                            null,
                            response.data.id,
                            role,
                            response.data.username
                    );
                    
                    // Small delay to ensure tokens are committed to storage
                    new android.os.Handler().postDelayed(() -> {
                        Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }, 500);
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Access denied: Received role: " + role, Toast.LENGTH_LONG).show();
                }



            }
        });

        viewModel.getError().observe(this, error -> {
            Toast.makeText(this, "Login Error: " + error, Toast.LENGTH_LONG).show();
        });
    }

}
