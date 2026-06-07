package com.example.quizapp.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.quizapp.data.local.AppDatabase;
import com.example.quizapp.data.local.dao.LeaderboardDao;
import com.example.quizapp.data.local.entity.LeaderboardEntity;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.LeaderboardEntry;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class LeaderboardRepository {
    private ApiService apiService;
    private LeaderboardDao leaderboardDao;

    public LeaderboardRepository(Context context) {
        this.apiService = RetrofitClient.getApi();
        this.leaderboardDao = AppDatabase.getInstance(context).leaderboardDao();
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboardLocal(String period) {
        return Transformations.map(leaderboardDao.getByPeriod(period), entities -> {
            List<LeaderboardEntry> list = new ArrayList<>();
            for (LeaderboardEntity e : entities) {
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.user_id = e.userId;
                entry.full_name = e.fullName;
                entry.score = e.score;
                entry.best_category = e.bestCategory;
                entry.rank = e.rank;
                list.add(entry);
            }
            return list;
        });
    }

    public void saveLeaderboardLocal(List<LeaderboardEntry> entries, String period) {
        List<LeaderboardEntity> entities = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            LeaderboardEntity e = new LeaderboardEntity();
            e.userId = entry.user_id;
            e.fullName = entry.full_name;
            e.score = entry.score;
            e.bestCategory = entry.best_category;
            e.rank = entry.rank > 0 ? entry.rank : (i + 1);
            e.period = period;
            entities.add(e);
        }
        
        AppDatabase.getInstance(null).getQueryExecutor().execute(() -> {
            leaderboardDao.deleteByPeriod(period);
            leaderboardDao.insertAll(entities);
        });
    }

    public Call<GenericResponse<List<LeaderboardEntry>>> getLeaderboardRemote(String period, int page) {
        return apiService.getLeaderboard(period, page);
    }
}

