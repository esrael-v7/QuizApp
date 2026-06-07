package com.example.quizapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.quizapp.data.local.entity.SyncActionEntity;
import java.util.List;

@Dao
public interface SyncActionDao {
    @Insert
    void insert(SyncActionEntity action);

    @Query("SELECT * FROM sync_actions WHERE isPending = 1 ORDER BY timestamp ASC")
    List<SyncActionEntity> getPendingActions();

    @Update
    void update(SyncActionEntity action);

    @Delete
    void delete(SyncActionEntity action);
}
