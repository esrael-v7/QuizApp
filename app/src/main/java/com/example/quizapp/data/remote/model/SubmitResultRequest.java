package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubmitResultRequest {
    @SerializedName(value = "category_id", alternate = {"categoryId"})
    public int category_id;
    
    @SerializedName(value = "total_questions", alternate = {"totalQuestions"})
    public int total_questions;
    
    @SerializedName(value = "correct_answers", alternate = {"correctAnswers"})
    public int correct_answers;
    
    @SerializedName(value = "time_taken_secs", alternate = {"timeTakenSecs"})
    public int time_taken_secs;

    public List<AnswerRecord> answers;

    public SubmitResultRequest(int category_id, int total_questions, int correct_answers, int time_taken_secs, List<AnswerRecord> answers) {
        this.category_id = category_id;
        this.total_questions = total_questions;
        this.correct_answers = correct_answers;
        this.time_taken_secs = time_taken_secs;
        this.answers = answers;
    }
}
