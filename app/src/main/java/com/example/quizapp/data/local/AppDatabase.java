package com.example.quizapp.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.quizapp.data.local.dao.CategoryDao;
import com.example.quizapp.data.local.dao.FavouriteDao;
import com.example.quizapp.data.local.dao.QuestionDao;
import com.example.quizapp.data.local.entity.CategoryEntity;
import com.example.quizapp.data.local.entity.FavouriteEntity;
import com.example.quizapp.data.local.entity.QuestionEntity;

@Database(entities = {QuestionEntity.class, CategoryEntity.class, FavouriteEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract QuestionDao questionDao();
    public abstract CategoryDao categoryDao();
    public abstract FavouriteDao favouriteDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "quizapp.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
