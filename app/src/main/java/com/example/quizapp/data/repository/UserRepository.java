package com.example.quizapp.data.repository;

import android.content.Context;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.StreakData;
import com.example.quizapp.data.remote.model.UserStats;

import retrofit2.Call;

public class UserRepository {
    private ApiService apiService;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApi();
    }

    public Call<GenericResponse<UserStats>> getUserStats() {
        return apiService.getUserStats();
    }

    public Call<GenericResponse<StreakData>> getStreakData() {
        return apiService.getStreakData();
    }

    public Call<GenericResponse<Void>> syncData() {
        return apiService.syncData();
    }
}
