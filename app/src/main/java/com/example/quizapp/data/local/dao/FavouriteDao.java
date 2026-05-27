package com.example.quizapp.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.quizapp.data.local.entity.FavouriteEntity;
import java.util.List;

@Dao
public interface FavouriteDao {
    @Query("SELECT * FROM favourites")
    LiveData<List<FavouriteEntity>> getAll();

    @Query("SELECT * FROM favourites WHERE category_name = :categoryName")
    LiveData<List<FavouriteEntity>> getByCategory(String categoryName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavouriteEntity favourite);

    @Query("DELETE FROM favourites WHERE question_id = :questionId")
    void deleteByQuestionId(int questionId);
}
