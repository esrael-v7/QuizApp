package com.example.quizapp.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class CategoryEntity {
    @PrimaryKey
    public int id;
    public String name;
    public String description;
    public String icon_name;
    public int question_count;

    public CategoryEntity(int id, String name, String description, String icon_name, int question_count) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon_name = icon_name;
        this.question_count = question_count;
    }
}
