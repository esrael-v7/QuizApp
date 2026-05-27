package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
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

        tokenManager = new TokenManager(this);
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            if (!email.isEmpty() && !password.isEmpty()) {
                viewModel.login(email, password);
            }
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getLoginResult().observe(this, response -> {
            if (response != null && response.data != null) {
                if ("admin".equals(response.data.role)) {
                    tokenManager.saveTokens(
                            response.data.token,
                            null,
                            response.data.id,
                            response.data.role,
                            response.data.username
                    );
                    startActivity(new Intent(this, AdminActivity.class));
                    finish();
                } else {
                    Snackbar.make(binding.getRoot(), "Access denied: Not an admin", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        viewModel.getError().observe(this, error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }
}
