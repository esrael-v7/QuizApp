package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class Question {
    @SerializedName("id")
    public int id;

    @SerializedName(value = "category_id", alternate = {"categoryId"})
    public int category_id;

    @SerializedName(value = "question_text", alternate = {"questionText"})
    public String question_text;

    @SerializedName(value = "option_a", alternate = {"optionA"})
    public String option_a;

    @SerializedName(value = "option_b", alternate = {"optionB"})
    public String option_b;

    @SerializedName(value = "option_c", alternate = {"optionC"})
    public String option_c;

    @SerializedName(value = "option_d", alternate = {"optionD"})
    public String option_d;

    @SerializedName(value = "correct_option", alternate = {"correctOption"})
    public String correct_option;

    @SerializedName(value = "explanation", alternate = {"explanationText"})
    public String explanation;

    @SerializedName(value = "difficulty", alternate = {"difficultyLevel"})
    public String difficulty;

}
