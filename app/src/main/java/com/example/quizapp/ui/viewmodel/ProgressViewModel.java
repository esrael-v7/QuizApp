package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.data.remote.model.CategoryScore;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.UserStats;
import com.example.quizapp.data.repository.QuizRepository;
import com.example.quizapp.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private QuizRepository quizRepository;
    private LiveData<UserStats> stats;
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        this.quizRepository = new QuizRepository(application);
        this.stats = userRepository.getUserStatsLocal();
    }

    public LiveData<UserStats> getStats() { return stats; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void syncData() {
        if (!com.example.quizapp.utils.NetworkUtils.isNetworkAvailable(getApplication())) {
            // No internet, fail silently without setting error if it's an auto-sync
            return;
        }
        loading.setValue(true);
        userRepository.syncData().enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                if (response.isSuccessful()) {
                    fetchStats(); // Refresh stats after sync
                } else {
                    loading.setValue(false);
                    error.setValue("Sync failed: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Sync failed: network error (" + t.getMessage() + ")");
            }
        });
    }


    private long lastFetchTime = 0;

    public void fetchStats() {
        // Prevent spamming requests (Rate limit 429 prevention)
        if (System.currentTimeMillis() - lastFetchTime < 30000 && stats.getValue() != null) {
            return;
        }

        if (!com.example.quizapp.utils.NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }

        if (stats.getValue() == null) {
            loading.setValue(true);
        }
        
        lastFetchTime = System.currentTimeMillis();
        // 1. Fetch Categories
        quizRepository.getCategoriesRemote().enqueue(new Callback<GenericResponse<List<Category>>>() {

            @Override
            public void onResponse(Call<GenericResponse<List<Category>>> call, Response<GenericResponse<List<Category>>> catResponse) {
                if (catResponse.isSuccessful() && catResponse.body() != null) {
                    List<Category> allCategories = catResponse.body().data;
                    quizRepository.saveCategoriesLocal(allCategories);
                    
                    // 2. Fetch User Stats
                    userRepository.getUserStatsRemote().enqueue(new Callback<GenericResponse<UserStats>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<UserStats>> call, Response<GenericResponse<UserStats>> response) {
                            loading.setValue(false);
                            if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                                UserStats userStats = response.body().data;
                                // Merge categories
                                mergeCategoriesWithStats(userStats, allCategories);
                                
                                // Identify weak areas if server didn't provide them
                                if (userStats.weak_areas == null || userStats.weak_areas.isEmpty()) {
                                    identifyWeakAreas(userStats);
                                }
                                
                                // Save locally
                                userRepository.saveUserStatsLocal(userStats);
                            } else if (response.code() == 429) {
                                error.setValue("Too many requests. Please wait.");
                            } else if (response.code() != 404 && response.code() != 204) {
                                error.setValue("Failed to load stats: " + response.code());
                            }
                        }



                        @Override
                        public void onFailure(Call<GenericResponse<UserStats>> call, Throwable t) {
                            loading.setValue(false);
                            error.setValue("Connection failed: " + t.getMessage());
                        }
                    });
                } else {
                    loading.setValue(false);
                    error.setValue("Failed to sync categories: " + catResponse.code());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Category>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed: " + t.getMessage());
            }
        });
    }



    private void calculateLocalStats(UserStats stats) {
        int totalPlayed = 0;
        double sumScore = 0;
        int categoriesPlayed = 0;
        
        for (CategoryScore cs : stats.scores_by_category) {
            if (cs.score > 0) {
                sumScore += cs.score;
                categoriesPlayed++;
                totalPlayed++; // Assume at least 1 quiz if score > 0
            }
        }
        
        if (categoriesPlayed > 0) {
            stats.total_quizzes = totalPlayed;
            stats.avg_score = sumScore / categoriesPlayed;
            stats.total_points = (int) (sumScore * 10);
        }
    }

    private void identifyWeakAreas(UserStats stats) {
        List<String> weak = new ArrayList<>();
        if (stats.scores_by_category != null) {
            for (CategoryScore cs : stats.scores_by_category) {
                // If they have played it (score > 0) and scored less than 70%
                // OR if it's 0 but they have played other categories (meaning they might be avoiding it)
                if (cs.score < 70) {
                    weak.add(cs.category_name);
                }
            }
        }
        stats.weak_areas = weak;
    }


    private void mergeCategoriesWithStats(UserStats stats, List<Category> allCategories) {
        if (allCategories == null) return;
        
        Map<String, Integer> scoreMap = new HashMap<>();
        if (stats.scores_by_category != null) {
            for (CategoryScore cs : stats.scores_by_category) {
                scoreMap.put(cs.category_name.toLowerCase(), cs.score);
            }
        }
        
        List<CategoryScore> mergedList = new ArrayList<>();
        for (Category cat : allCategories) {
            CategoryScore cs = new CategoryScore();
            cs.category_name = cat.name;
            // Use the score if it exists, otherwise 0
            Integer score = scoreMap.get(cat.name.toLowerCase());
            cs.score = (score != null) ? score : 0;
            mergedList.add(cs);
        }
        stats.scores_by_category = mergedList;
    }

    public void deleteAccount(Callback<GenericResponse<Void>> callback) {
        userRepository.deleteAccount().enqueue(callback);
    }
}
