package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class MyRank {
    @SerializedName("rank")
    public int rank;
    
    @SerializedName(value = "score", alternate = {"points"})
    public int score;
    
    @SerializedName(value = "best_category", alternate = {"bestCategory"})
    public String best_category;
}
