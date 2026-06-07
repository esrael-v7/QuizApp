package com.example.quizapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.quizapp.data.local.entity.UserStatsEntity;

@Dao
public interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    LiveData<UserStatsEntity> getStats();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserStatsEntity stats);

    @Query("DELETE FROM user_stats")
    void delete();
}
