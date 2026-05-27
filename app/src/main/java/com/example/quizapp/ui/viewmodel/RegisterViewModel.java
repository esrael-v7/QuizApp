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

public class RegisterViewModel extends ViewModel {
    private AuthRepository repository;
    private MutableLiveData<GenericResponse<AuthData>> registerResult = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public RegisterViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<GenericResponse<AuthData>> getRegisterResult() { return registerResult; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void register(String username, String email, String password) {
        loading.setValue(true);
        repository.register(username, email, password).enqueue(new Callback<GenericResponse<AuthData>>() {
            @Override
            public void onResponse(Call<GenericResponse<AuthData>> call, Response<GenericResponse<AuthData>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(response.body());
                } else {
                    error.setValue("Registration failed. Email might already be in use.");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<AuthData>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed. Check your network.");
            }
        });
    }
}
