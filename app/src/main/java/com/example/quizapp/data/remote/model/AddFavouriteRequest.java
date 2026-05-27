package com.example.quizapp.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class AddFavouriteRequest {
    @SerializedName("questionId")
    private int questionId;

    public AddFavouriteRequest(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }
}
