package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category_scores")
public class CategoryScoreEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String categoryName;
    public int score;

    public CategoryScoreEntity(String categoryName, int score) {
        this.categoryName = categoryName;
        this.score = score;
    }
}
