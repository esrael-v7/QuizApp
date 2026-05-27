package com.example.quizapp.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.quizapp.data.local.entity.QuestionEntity;
import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM questions WHERE categoryId = :catId ORDER BY RANDOM() LIMIT :limit")
    List<QuestionEntity> getRandomByCategory(int catId, int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<QuestionEntity> questions);

    @Query("DELETE FROM questions WHERE categoryId = :catId")
    void deleteByCategory(int catId);
}
