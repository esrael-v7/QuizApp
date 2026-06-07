package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserStats {
    @SerializedName("total_quizzes")
    public int total_quizzes;
    
    @SerializedName("avg_score")
    public double avg_score;
    
    @SerializedName("current_streak")
    public int current_streak;
    
    @SerializedName("best_streak")
    public int best_streak;
    
    @SerializedName("total_points")
    public int total_points;
    
    @SerializedName("scores_by_category")
    public List<CategoryScore> scores_by_category;
    
    @SerializedName("weak_areas")
    public List<String> weak_areas;

    @SerializedName("user_level")
    public int user_level;

    @SerializedName("last_synced_at")
    public String last_synced_at;

}
