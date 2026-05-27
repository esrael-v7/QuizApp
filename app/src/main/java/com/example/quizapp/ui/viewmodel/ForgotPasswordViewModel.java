package com.example.quizapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.ForgotPasswordRequest;
import com.example.quizapp.data.remote.model.ResetPasswordRequest;
import com.example.quizapp.data.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
    private AuthRepository repository;
    private MutableLiveData<Boolean> otpSent = new MutableLiveData<>();
    private MutableLiveData<Boolean> passwordReset = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ForgotPasswordViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<Boolean> isOtpSent() { return otpSent; }
    public LiveData<Boolean> isPasswordReset() { return passwordReset; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void sendOtp(String email) {
        loading.setValue(true);
        repository.forgotPassword(email).enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    otpSent.setValue(true);
                } else {
                    try {
                        // Try to get the error message from the backend JSON
                        String errorBody = response.errorBody().string();
                        error.setValue("Error: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        error.setValue("Failed to send code (Status: " + response.code() + ")");
                    }
                }
            }


            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }

    public void resetPassword(String email, String otp, String newPassword) {
        loading.setValue(true);
        repository.resetPassword(email, otp, newPassword).enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    passwordReset.setValue(true);
                } else {
                    error.setValue("Invalid code or reset failed");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }
}
