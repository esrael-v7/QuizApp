package com.example.quizapp.data.repository;

import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.AuthData;
import com.example.quizapp.data.remote.model.ForgotPasswordRequest;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.GoogleLoginRequest;
import com.example.quizapp.data.remote.model.LoginRequest;
import com.example.quizapp.data.remote.model.RegisterRequest;
import com.example.quizapp.data.remote.model.ResetPasswordRequest;



import retrofit2.Call;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getApi();
    }

    public Call<GenericResponse<AuthData>> login(String email, String password) {
        return apiService.login(new LoginRequest(email, password));
    }

    public Call<GenericResponse<AuthData>> googleLogin(String idToken) {
        return apiService.googleLogin(new GoogleLoginRequest(idToken));
    }


    public Call<GenericResponse<AuthData>> register(String username, String email, String password) {
        return apiService.register(new RegisterRequest(username, email, password));
    }

    public Call<GenericResponse<Void>> forgotPassword(String email) {
        return apiService.forgotPassword(new ForgotPasswordRequest(email));
    }

    public Call<GenericResponse<Void>> resetPassword(String email, String otp, String newPassword) {
        return apiService.resetPassword(new ResetPasswordRequest(email, otp, newPassword));
    }
}

