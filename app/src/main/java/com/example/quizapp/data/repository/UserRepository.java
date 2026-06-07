package com.example.quizapp.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.quizapp.data.local.AppDatabase;

import com.example.quizapp.data.local.dao.CategoryScoreDao;
import com.example.quizapp.data.local.dao.UserStatsDao;
import com.example.quizapp.data.local.entity.CategoryScoreEntity;
import com.example.quizapp.data.local.entity.UserStatsEntity;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.CategoryScore;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.StreakData;
import com.example.quizapp.data.remote.model.UserStats;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class UserRepository {
    private ApiService apiService;
    private UserStatsDao userStatsDao;
    private CategoryScoreDao categoryScoreDao;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApi();
        AppDatabase db = AppDatabase.getInstance(context);
        this.userStatsDao = db.userStatsDao();
        this.categoryScoreDao = db.categoryScoreDao();
    }

    public LiveData<UserStats> getUserStatsLocal() {
        return Transformations.switchMap(userStatsDao.getStats(), entity -> {
            MutableLiveData<UserStats> result = new MutableLiveData<>();
            
            if (entity == null) {
                result.postValue(null);
                return result;
            }

            UserStats stats = new UserStats();
            stats.total_quizzes = entity.total_quizzes;
            stats.avg_score = entity.avg_score;
            stats.current_streak = entity.current_streak;
            stats.total_points = entity.total_points;
            stats.user_level = entity.user_level;
            stats.last_synced_at = entity.last_synced_at;
            
            if (entity.weak_areas_csv != null && !entity.weak_areas_csv.isEmpty()) {
                stats.weak_areas = new ArrayList<>(java.util.Arrays.asList(entity.weak_areas_csv.split(",")));
            } else {
                stats.weak_areas = new ArrayList<>();
            }

            // Fetch scores for this UserStats
            return Transformations.map(categoryScoreDao.getAll(), scores -> {
                List<CategoryScore> list = new ArrayList<>();
                for (CategoryScoreEntity se : scores) {
                    CategoryScore s = new CategoryScore();
                    s.category_name = se.categoryName;
                    s.score = se.score;
                    list.add(s);
                }
                stats.scores_by_category = list;
                return stats;
            });
        });
    }

    public void saveUserStatsLocal(UserStats stats) {
        if (stats == null) return;
        UserStatsEntity entity = new UserStatsEntity();
        entity.total_quizzes = stats.total_quizzes;
        entity.avg_score = stats.avg_score;
        entity.current_streak = stats.current_streak;
        entity.total_points = stats.total_points;
        entity.user_level = stats.user_level;
        entity.last_synced_at = stats.last_synced_at;
        
        if (stats.weak_areas != null && !stats.weak_areas.isEmpty()) {
            entity.weak_areas_csv = String.join(",", stats.weak_areas);
        }
        
        AppDatabase db = AppDatabase.getInstance(null);
        db.getQueryExecutor().execute(() -> {
            db.runInTransaction(() -> {
                userStatsDao.insert(entity);
                if (stats.scores_by_category != null) {
                    categoryScoreDao.deleteAll();
                    List<CategoryScoreEntity> scores = new ArrayList<>();
                    for (CategoryScore s : stats.scores_by_category) {
                        scores.add(new CategoryScoreEntity(s.category_name, s.score));
                    }
                    categoryScoreDao.insertAll(scores);
                }
            });
        });
    }



    public Call<GenericResponse<UserStats>> getUserStatsRemote() {
        return apiService.getUserStats();
    }


    public Call<GenericResponse<StreakData>> getStreakData() {
        return apiService.getStreakData();
    }

    public Call<GenericResponse<Void>> syncData() {
        return apiService.syncData();
    }
}
