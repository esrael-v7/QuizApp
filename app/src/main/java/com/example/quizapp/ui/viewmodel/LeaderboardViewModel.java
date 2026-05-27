package com.example.quizapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quizapp.data.remote.model.LeaderboardEntry;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.repository.LeaderboardRepository;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardViewModel extends ViewModel {
    private LeaderboardRepository repository;
    private MutableLiveData<List<LeaderboardEntry>> leaderboard = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LeaderboardViewModel() {
        this.repository = new LeaderboardRepository();
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboard() { return leaderboard; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void fetchLeaderboard(String period) {
        if (leaderboard.getValue() != null && !leaderboard.getValue().isEmpty()) {
            return;
        }
        loading.setValue(true);
        repository.getLeaderboard(period, 1).enqueue(new Callback<GenericResponse<List<LeaderboardEntry>>>() {

            @Override
            public void onResponse(Call<GenericResponse<List<LeaderboardEntry>>> call, Response<GenericResponse<List<LeaderboardEntry>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    leaderboard.setValue(response.body().data);
                } else {
                    error.setValue("Failed to load leaderboard");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<LeaderboardEntry>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }
}
