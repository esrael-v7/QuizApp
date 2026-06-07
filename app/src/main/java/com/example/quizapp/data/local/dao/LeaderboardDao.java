package com.example.quizapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.quizapp.data.local.entity.LeaderboardEntity;
import java.util.List;

@Dao
public interface LeaderboardDao {
    @Query("SELECT * FROM leaderboard WHERE period = :period ORDER BY rank ASC")
    LiveData<List<LeaderboardEntity>> getByPeriod(String period);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LeaderboardEntity> entries);

    @Query("DELETE FROM leaderboard WHERE period = :period")
    void deleteByPeriod(String period);
}
