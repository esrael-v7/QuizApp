package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favourites")
public class FavouriteEntity {
    @PrimaryKey
    public int question_id;
    public int user_id;
    public String question_text;
    public String option_a;
    public String option_b;
    public String option_c;
    public String option_d;
    public String correct_option;
    public String explanation;
    public String difficulty;
    public String category_name;
}
