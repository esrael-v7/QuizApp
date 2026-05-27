package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class SubmitScoreRequest {
    @SerializedName(value = "category_id", alternate = {"categoryId"})
    public int categoryId;
    
    @SerializedName("score")
    public int score;
    
    @SerializedName(value = "total_questions", alternate = {"totalQuestions"})
    public int totalQuestions;

    public SubmitScoreRequest(int categoryId, int score, int totalQuestions) {
        this.categoryId = categoryId;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }
}
