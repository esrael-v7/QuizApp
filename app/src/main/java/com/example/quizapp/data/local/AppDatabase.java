package com.example.quizapp.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.quizapp.data.local.dao.CategoryDao;
import com.example.quizapp.data.local.dao.CategoryScoreDao;
import com.example.quizapp.data.local.dao.FavouriteDao;
import com.example.quizapp.data.local.dao.LeaderboardDao;
import com.example.quizapp.data.local.dao.QuestionDao;
import com.example.quizapp.data.local.dao.UserStatsDao;
import com.example.quizapp.data.local.dao.SyncActionDao;
import com.example.quizapp.data.local.entity.CategoryEntity;
import com.example.quizapp.data.local.entity.CategoryScoreEntity;
import com.example.quizapp.data.local.entity.FavouriteEntity;
import com.example.quizapp.data.local.entity.LeaderboardEntity;
import com.example.quizapp.data.local.entity.QuestionEntity;
import com.example.quizapp.data.local.entity.SyncActionEntity;
import com.example.quizapp.data.local.entity.UserStatsEntity;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

@Database(entities = {QuestionEntity.class, CategoryEntity.class, FavouriteEntity.class, UserStatsEntity.class, CategoryScoreEntity.class, LeaderboardEntity.class, SyncActionEntity.class}, version = 6)

public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract QuestionDao questionDao();
    public abstract CategoryDao categoryDao();
    public abstract FavouriteDao favouriteDao();
    public abstract UserStatsDao userStatsDao();
    public abstract CategoryScoreDao categoryScoreDao();
    public abstract LeaderboardDao leaderboardDao();
    public abstract SyncActionDao syncActionDao();




    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    // Initialize SQLCipher
                    SQLiteDatabase.loadLibs(context);
                    byte[] passphrase = SQLiteDatabase.getBytes("quiz-secure-passphrase".toCharArray());
                    SupportFactory factory = new SupportFactory(passphrase);

                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "quizapp_final.db")

                            .openHelperFactory(factory)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}

