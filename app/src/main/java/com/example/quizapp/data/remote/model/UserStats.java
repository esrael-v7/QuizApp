package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserStats {
    @SerializedName(value = "total_quizzes", alternate = {"totalQuizzes"})
    public int total_quizzes;
    
    @SerializedName(value = "avg_score", alternate = {"avgScore"})
    public double avg_score;
    
    @SerializedName(value = "current_streak", alternate = {"currentStreak"})
    public int current_streak;
    
    @SerializedName(value = "best_streak", alternate = {"bestStreak"})
    public int best_streak;
    
    @SerializedName(value = "total_points", alternate = {"totalPoints"})
    public int total_points;
    
    @SerializedName(value = "scores_by_category", alternate = {"scoresByCategory"})
    public List<CategoryScore> scores_by_category;
    
    @SerializedName(value = "weak_areas", alternate = {"weakAreas"})
    public List<String> weak_areas;

    @SerializedName(value = "user_level", alternate = {"level", "userLevel"})
    public int user_level;

    @SerializedName(value = "last_synced_at", alternate = {"lastSyncedAt"})
    public String last_synced_at;
}
