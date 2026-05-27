package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class CategoryScore {
    @SerializedName(value = "category_name", alternate = {"categoryName"})
    public String category_name;

    public int score;
}
