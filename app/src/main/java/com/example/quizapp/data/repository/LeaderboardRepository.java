package com.example.quizapp.data.repository;

import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.LeaderboardEntry;
import java.util.List;
import retrofit2.Call;

public class LeaderboardRepository {
    private ApiService apiService;

    public LeaderboardRepository() {
        this.apiService = RetrofitClient.getApi();
    }

    public Call<GenericResponse<List<LeaderboardEntry>>> getLeaderboard(String period, int page) {
        return apiService.getLeaderboard(period, page);
    }
}
