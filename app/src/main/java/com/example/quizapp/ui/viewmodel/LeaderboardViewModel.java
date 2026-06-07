package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.LeaderboardEntry;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.repository.LeaderboardRepository;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardViewModel extends AndroidViewModel {
    private LeaderboardRepository repository;
    private MutableLiveData<List<LeaderboardEntry>> leaderboard = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LeaderboardViewModel(@NonNull Application application) {
        super(application);
        this.repository = new LeaderboardRepository(application);
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboard() { return leaderboard; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    private long lastFetchTime = 0;

    public void fetchLeaderboard(String period) {
        // First try to load from local DB for instant UI
        repository.getLeaderboardLocal(period).observeForever(localEntries -> {
            if (localEntries != null && !localEntries.isEmpty() && (leaderboard.getValue() == null || leaderboard.getValue().isEmpty())) {
                leaderboard.postValue(localEntries);
            }
        });

        // Prevent spamming requests (Rate limit 429 prevention)
        if (System.currentTimeMillis() - lastFetchTime < 30000 && leaderboard.getValue() != null && !leaderboard.getValue().isEmpty()) {
            return;
        }
        
        loading.setValue(true);
        lastFetchTime = System.currentTimeMillis();
        repository.getLeaderboardRemote(period, 1).enqueue(new Callback<GenericResponse<List<LeaderboardEntry>>>() {

            @Override
            public void onResponse(Call<GenericResponse<List<LeaderboardEntry>>> call, Response<GenericResponse<List<LeaderboardEntry>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<LeaderboardEntry> data = response.body().data;
                    leaderboard.setValue(data);
                    // Save to local DB for next time
                    repository.saveLeaderboardLocal(data, period);
                } else if (response.code() == 429) {
                    error.setValue("Too many requests. Please wait.");
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
