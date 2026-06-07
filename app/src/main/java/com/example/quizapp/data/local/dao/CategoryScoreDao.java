package com.example.quizapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.quizapp.data.local.entity.CategoryScoreEntity;
import java.util.List;

@Dao
public interface CategoryScoreDao {
    @Query("SELECT * FROM category_scores")
    LiveData<List<CategoryScoreEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CategoryScoreEntity> scores);

    @Query("DELETE FROM category_scores")
    void deleteAll();
}
