package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_stats")
public class UserStatsEntity {
    @PrimaryKey
    public int id = 1;
    public int total_quizzes;
    public double avg_score;
    public int current_streak;
    public int total_points;
    public int user_level;
    public String last_synced_at;
    public String weak_areas_csv; // Store as comma-separated values
}

