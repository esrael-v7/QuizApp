package com.example.quizapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizapp.data.remote.model.AuthData;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {
    private AuthRepository repository;
    private MutableLiveData<GenericResponse<AuthData>> loginResult = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LoginViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<GenericResponse<AuthData>> getLoginResult() { return loginResult; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void login(String email, String password) {
        loading.setValue(true);
        repository.login(email, password).enqueue(new Callback<GenericResponse<AuthData>>() {
            @Override
            public void onResponse(Call<GenericResponse<AuthData>> call, Response<GenericResponse<AuthData>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                } else if (response.code() == 401) {
                    error.setValue("Invalid email or password");
                } else {
                    error.setValue("Login failed. Please try again.");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<AuthData>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed. Check your network.");
            }
        });
    }

    public void googleLogin(String idToken) {
        loading.setValue(true);
        repository.googleLogin(idToken).enqueue(new Callback<GenericResponse<AuthData>>() {
            @Override
            public void onResponse(Call<GenericResponse<AuthData>> call, Response<GenericResponse<AuthData>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                } else {
                    error.setValue("Google login failed.");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<AuthData>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed.");
            }
        });
    }

}
