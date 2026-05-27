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
    private MutableLiveData<UserStats> stats = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ProgressViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        this.quizRepository = new QuizRepository(application);
    }

    public LiveData<UserStats> getStats() { return stats; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void syncData() {
        loading.setValue(true);
        userRepository.syncData().enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                if (response.isSuccessful()) {
                    fetchStats(); // Refresh stats after sync
                } else {
                    loading.setValue(false);
                    error.setValue("Sync failed");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Sync failed: network error");
            }
        });
    }

    public void fetchStats() {
        if (stats.getValue() != null && stats.getValue().total_quizzes > 0) {
            return;
        }
        loading.setValue(true);
        // 1. Fetch Categories

        quizRepository.getCategories().enqueue(new Callback<GenericResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Category>>> call, Response<GenericResponse<List<Category>>> catResponse) {
                if (catResponse.isSuccessful() && catResponse.body() != null) {
                    List<Category> allCategories = catResponse.body().data;
                    
                    // 2. Fetch User Stats
                    userRepository.getUserStats().enqueue(new Callback<GenericResponse<UserStats>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<UserStats>> call, Response<GenericResponse<UserStats>> response) {
                            loading.setValue(false);
                            UserStats userStats;
                            if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                                userStats = response.body().data;
                            } else if (response.code() == 404 || response.code() == 204) {
                                userStats = new UserStats();
                            } else {
                                error.setValue("Failed to load stats");
                                return;
                            }
                            
                            // 3. Merge: Ensure all categories from Home screen are shown here with scores
                            mergeCategoriesWithStats(userStats, allCategories);
                            
                            // 4. Fallback calculation: If stats are zero but categories have scores, calculate locally
                            if (userStats.total_quizzes == 0 && !userStats.scores_by_category.isEmpty()) {
                                calculateLocalStats(userStats);
                            }
                            
                            // 5. Weak Areas fallback: If null, identify categories with < 60% score
                            if (userStats.weak_areas == null || userStats.weak_areas.isEmpty()) {
                                identifyWeakAreas(userStats);
                            }
                            
                            stats.setValue(userStats);
                        }

                        @Override
                        public void onFailure(Call<GenericResponse<UserStats>> call, Throwable t) {
                            loading.setValue(false);
                            error.setValue("Connection failed");
                        }
                    });
                } else {
                    loading.setValue(false);
                    error.setValue("Failed to sync categories");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Category>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
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
                if (cs.score > 0 && cs.score < 60) {
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
}
