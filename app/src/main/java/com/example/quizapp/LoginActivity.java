package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.quizapp.data.remote.model.AuthData;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.databinding.ActivityLoginBinding;
import com.example.quizapp.ui.viewmodel.LoginViewModel;
import com.example.quizapp.utils.TokenManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;


public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;
    private TokenManager tokenManager;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        tokenManager = TokenManager.getInstance(this);
        if (tokenManager.isLoggedIn()) {

            if (tokenManager.getRole().equalsIgnoreCase("admin")) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, MainActivity.class));
            }
            finish();
            return;
        }




        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setupListeners();
        observeViewModel();
    }


    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                viewModel.login(email, password);
            }
        });

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        binding.btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());

        binding.tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
            overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        });


        binding.tvAdminLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminLoginActivity.class));
        });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;
        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            isValid = false;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        } else {
            binding.tilPassword.setError(null);
        }
        return isValid;
    }

    private void signInWithGoogle() {
        // Force account selection by signing out first
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String idToken = account.getIdToken();
                viewModel.googleLogin(idToken);
            }
        } catch (ApiException e) {
            Snackbar.make(binding.getRoot(), "Google sign in failed: " + e.getStatusCode(), Snackbar.LENGTH_LONG).show();
        }
    }

    private void observeViewModel() {

        viewModel.getLoginResult().observe(this, response -> {
            if (response != null && response.data != null) {
                tokenManager.saveTokens(
                        response.data.token,
                        null,
                        response.data.id,
                        response.data.role,
                        response.data.username
                );
                
                if (response.data.role != null && response.data.role.equalsIgnoreCase("admin")) {
                    startActivity(new Intent(this, AdminActivity.class));
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
                
                overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                finish();
            }
        });


        viewModel.getError().observe(this, errorMessage -> {
            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
        });

        viewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.btnLogin.setEnabled(false);
                binding.loadingIndicator.setVisibility(View.VISIBLE);
            } else {
                binding.btnLogin.setEnabled(true);
                binding.loadingIndicator.setVisibility(View.GONE);
            }
        });
    }
}

