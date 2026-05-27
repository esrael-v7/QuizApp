package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardEntry {
    @SerializedName("rank")
    public int rank;
    
    @SerializedName(value = "user_id", alternate = {"userId", "id"})
    public int user_id;
    
    @SerializedName(value = "full_name", alternate = {"fullName", "username"})
    public String full_name;
    
    @SerializedName(value = "score", alternate = {"points", "total_points"})
    public String score; // String because server sends "90" in quotes
    
    @SerializedName(value = "best_category", alternate = {"bestCategory"})
    public String best_category;
    
    @SerializedName("avatar_initials")
    public String avatar_initials;
    
    @SerializedName("quizzes_taken")
    public int quizzes_taken;
}
