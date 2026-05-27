package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions", indices = {@Index("categoryId")})
public class QuestionEntity {
    @PrimaryKey
    public int id;
    public int categoryId;
    public String question_text;
    public String option_a;
    public String option_b;
    public String option_c;
    public String option_d;
    public String correct_option;
    public String explanation;
    public String difficulty;

    public QuestionEntity(int id, int categoryId, String question_text, String option_a, String option_b, String option_c, String option_d, String correct_option, String explanation, String difficulty) {
        this.id = id;
        this.categoryId = categoryId;
        this.question_text = question_text;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.correct_option = correct_option;
        this.explanation = explanation;
        this.difficulty = difficulty;
    }
}
