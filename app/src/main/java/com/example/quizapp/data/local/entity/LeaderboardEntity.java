package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "leaderboard")
public class LeaderboardEntity {
    @PrimaryKey(autoGenerate = true)
    public int localId;
    public int userId;
    public String fullName;
    public String score;
    public String bestCategory;
    public int rank;
    public String period; // "today", "week", "alltime"
}
