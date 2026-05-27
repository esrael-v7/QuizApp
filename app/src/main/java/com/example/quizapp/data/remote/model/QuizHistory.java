package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class QuizHistory {
    @SerializedName(value = "id", alternate = {"session_id"})
    public int session_id;
    
    @SerializedName(value = "category_name", alternate = {"categoryName"})
    public String category_name;
    
    @SerializedName(value = "completed_at", alternate = {"completedAt"})
    public String completed_at;
    
    @SerializedName(value = "correct_answers", alternate = {"score"})
    public int correct_answers;
    
    @SerializedName(value = "total_questions", alternate = {"totalQuestions"})
    public int total_questions;
    
    @SerializedName(value = "score_percentage", alternate = {"scorePercentage"})
    public Double score_percentage;
}
