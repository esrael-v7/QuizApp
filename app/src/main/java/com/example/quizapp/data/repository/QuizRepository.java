package com.example.quizapp.data.repository;

import android.content.Context;
import com.example.quizapp.data.local.AppDatabase;
import com.example.quizapp.data.local.dao.CategoryDao;
import com.example.quizapp.data.local.dao.QuestionDao;
import com.example.quizapp.data.local.entity.CategoryEntity;
import com.example.quizapp.data.local.entity.QuestionEntity;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.AddFavouriteRequest;
import com.example.quizapp.data.remote.model.MessageResponse;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.remote.model.ResultResponse;
import com.example.quizapp.data.remote.model.SubmitResultRequest;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

public class QuizRepository {
    private ApiService apiService;
    private CategoryDao categoryDao;
    private QuestionDao questionDao;

    public QuizRepository(Context context) {
        this.apiService = RetrofitClient.getApi();
        AppDatabase db = AppDatabase.getInstance(context);
        this.categoryDao = db.categoryDao();
        this.questionDao = db.questionDao();
    }

    public Call<GenericResponse<List<Category>>> getCategories() {
        return apiService.getCategories();
    }

    public Call<GenericResponse<List<Question>>> getQuestions(int categoryId, int page, int limit) {
        return apiService.getQuestions(categoryId, page, limit);
    }

    public Call<GenericResponse<Void>> submitScore(SubmitScoreRequest request) {
        return apiService.submitScore(request);
    }

    public Call<ResultResponse> submitResult(SubmitResultRequest request) {
        return apiService.submitDetailedResult(request);
    }

    public Call<GenericResponse<List<Question>>> getFavourites() {
        return apiService.getFavourites();
    }

    public Call<GenericResponse<MessageResponse>> removeFavourite(int questionId) {
        return apiService.removeFavourite(questionId);
    }

    public Call<GenericResponse<MessageResponse>> addFavourite(int questionId) {
        return apiService.addFavourite(new AddFavouriteRequest(questionId));
    }

    // Local DB methods
    public void saveCategoriesLocal(List<Category> remoteCategories) {
        List<CategoryEntity> entities = new ArrayList<>();
        for (Category c : remoteCategories) {
            entities.add(new CategoryEntity(c.id, c.name, c.description, c.icon_name, c.question_count));
        }
        AppDatabase.getInstance(null).getQueryExecutor().execute(() -> {
            categoryDao.deleteAll();
            categoryDao.insertAll(entities);
        });
    }

    public void saveQuestionsLocal(List<Question> remoteQuestions, int categoryId) {
        List<QuestionEntity> entities = new ArrayList<>();
        for (Question q : remoteQuestions) {
            entities.add(new QuestionEntity(q.id, q.category_id, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.explanation, q.difficulty));
        }
        AppDatabase.getInstance(null).getQueryExecutor().execute(() -> {
            questionDao.deleteByCategory(categoryId);
            questionDao.insertAll(entities);
        });
    }
}
