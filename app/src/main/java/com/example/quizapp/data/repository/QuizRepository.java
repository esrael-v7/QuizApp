package com.example.quizapp.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.quizapp.data.local.AppDatabase;
import com.example.quizapp.data.local.dao.CategoryDao;
import com.example.quizapp.data.local.dao.QuestionDao;
import com.example.quizapp.data.local.dao.SyncActionDao;
import com.example.quizapp.data.local.entity.CategoryEntity;
import com.example.quizapp.data.local.entity.QuestionEntity;
import com.example.quizapp.data.local.entity.SyncActionEntity;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.AddFavouriteRequest;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;
import com.example.quizapp.sync.SyncWorker;
import com.example.quizapp.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizRepository {
    private final ApiService apiService;
    private final CategoryDao categoryDao;
    private final QuestionDao questionDao;
    private final com.example.quizapp.data.local.dao.FavouriteDao favouriteDao;
    private final SyncActionDao syncActionDao;
    private final Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Gson gson = new Gson();

    public QuizRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getApi();
        AppDatabase db = AppDatabase.getInstance(context);
        this.categoryDao = db.categoryDao();
        this.questionDao = db.questionDao();
        this.favouriteDao = db.favouriteDao();
        this.syncActionDao = db.syncActionDao();
    }

    // --- Categories ---

    public LiveData<List<Category>> getCategories() {
        refreshCategories(); // Try to update from network
        return Transformations.map(categoryDao.getAll(), entities -> {
            List<Category> list = new ArrayList<>();
            for (CategoryEntity e : entities) {
                Category c = new Category();
                c.id = e.id;
                c.name = e.name;
                c.description = e.description;
                c.icon_name = e.icon_name;
                c.question_count = e.question_count;
                list.add(c);
            }
            return list;
        });
    }

    private void refreshCategories() {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getCategories().enqueue(new Callback<GenericResponse<List<Category>>>() {
                @Override
                public void onResponse(Call<GenericResponse<List<Category>>> call, Response<GenericResponse<List<Category>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        saveCategoriesLocal(response.body().data);
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Category>>> call, Throwable t) {}
            });
        }
    }

    // --- Questions ---

    public void getQuestions(int catId, int limit, OnLocalDataLoadedListener<List<Question>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getQuestions(catId, 1, limit).enqueue(new Callback<GenericResponse<List<Question>>>() {
                @Override
                public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                        saveQuestionsLocal(response.body().data, catId);
                    }
                    loadQuestionsLocal(catId, limit, callback);
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                    loadQuestionsLocal(catId, limit, callback);
                }
            });
        } else {
            loadQuestionsLocal(catId, limit, callback);
        }
    }

    private void loadQuestionsLocal(int catId, int limit, OnLocalDataLoadedListener<List<Question>> callback) {
        executor.execute(() -> {
            List<QuestionEntity> entities = questionDao.getRandomByCategory(catId, limit);
            List<Question> list = new ArrayList<>();
            for (QuestionEntity e : entities) {
                Question q = new Question();
                q.id = e.id;
                q.category_id = e.categoryId;
                q.question_text = e.question_text;
                q.option_a = e.option_a;
                q.option_b = e.option_b;
                q.option_c = e.option_c;
                q.option_d = e.option_d;
                q.correct_option = e.correct_option;
                q.explanation = e.explanation;
                q.difficulty = e.difficulty;
                list.add(q);
            }
            callback.onLoaded(list);
        });
    }

    public interface OnLocalDataLoadedListener<T> {
        void onLoaded(T data);
    }

    // --- Sync Operations ---

    public void submitScore(SubmitScoreRequest request) {
        String data = gson.toJson(request);
        SyncActionEntity action = new SyncActionEntity("SUBMIT_SCORE", data);
        saveAndScheduleSync(action);
    }

    public void addFavourite(int questionId) {
        // If question exists in local questions table, we can copy it to favourites
        executor.execute(() -> {
            QuestionEntity qe = questionDao.getById(questionId);
            if (qe != null) {
                com.example.quizapp.data.local.entity.FavouriteEntity fe = new com.example.quizapp.data.local.entity.FavouriteEntity();
                fe.question_id = qe.id;
                fe.question_text = qe.question_text;
                fe.option_a = qe.option_a;
                fe.option_b = qe.option_b;
                fe.option_c = qe.option_c;
                fe.option_d = qe.option_d;
                fe.correct_option = qe.correct_option;
                fe.explanation = qe.explanation;
                fe.difficulty = qe.difficulty;
                favouriteDao.insert(fe);
            }
        });

        String data = gson.toJson(new AddFavouriteRequest(questionId));
        SyncActionEntity action = new SyncActionEntity("ADD_FAVOURITE", data);
        saveAndScheduleSync(action);
    }

    public void removeFavourite(int questionId) {
        executor.execute(() -> favouriteDao.deleteByQuestionId(questionId));

        SyncActionEntity action = new SyncActionEntity("REMOVE_FAVOURITE", String.valueOf(questionId));
        saveAndScheduleSync(action);
    }

    public Call<GenericResponse<List<Question>>> getFavouritesRemote() {
        return apiService.getFavourites();
    }

    public LiveData<List<Question>> getFavouritesLocal() {
        return Transformations.map(favouriteDao.getAll(), entities -> {
            List<Question> list = new ArrayList<>();
            for (com.example.quizapp.data.local.entity.FavouriteEntity e : entities) {
                Question q = new Question();
                q.id = e.question_id;
                q.question_text = e.question_text;
                q.option_a = e.option_a;
                q.option_b = e.option_b;
                q.option_c = e.option_c;
                q.option_d = e.option_d;
                q.correct_option = e.correct_option;
                q.explanation = e.explanation;
                q.difficulty = e.difficulty;
                list.add(q);
            }
            return list;
        });
    }

    public void saveFavouritesLocal(List<Question> questions) {
        if (questions == null) return;
        executor.execute(() -> {
            for (Question q : questions) {
                com.example.quizapp.data.local.entity.FavouriteEntity fe = new com.example.quizapp.data.local.entity.FavouriteEntity();
                fe.question_id = q.id;
                fe.question_text = q.question_text;
                fe.option_a = q.option_a;
                fe.option_b = q.option_b;
                fe.option_c = q.option_c;
                fe.option_d = q.option_d;
                fe.correct_option = q.correct_option;
                fe.explanation = q.explanation;
                fe.difficulty = q.difficulty;
                favouriteDao.insert(fe);
            }
        });
    }

    private void saveAndScheduleSync(SyncActionEntity action) {
        android.util.Log.d("SYNC_DEBUG", "Scheduling sync for: " + action.actionType);
        executor.execute(() -> {
            syncActionDao.insert(action);
            scheduleSync();
        });
    }

    public void scheduleSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueue(syncRequest);
    }

    public Call<GenericResponse<List<Category>>> getCategoriesRemote() {
        return apiService.getCategories();
    }

    public Call<GenericResponse<List<Question>>> getQuestionsRemote(int categoryId, int page, int limit) {
        return apiService.getQuestions(categoryId, page, limit);
    }

    // --- Local Helpers ---

    public void saveCategoriesLocal(List<Category> remoteCategories) {
        if (remoteCategories == null) return;
        List<CategoryEntity> entities = new ArrayList<>();
        for (Category c : remoteCategories) {
            entities.add(new CategoryEntity(c.id, c.name, c.description, c.icon_name, c.question_count));
        }
        executor.execute(() -> {
            categoryDao.deleteAll();
            categoryDao.insertAll(entities);

            // Automatically pre-fetch questions for all categories for offline use
            for (Category cat : remoteCategories) {
                prefetchQuestions(cat.id);
            }
        });
    }

    private void prefetchQuestions(int catId) {
        apiService.getQuestions(catId, 1, 20).enqueue(new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    saveQuestionsLocal(response.body().data, catId);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                // Ignore prefetch failures
            }
        });
    }

    public void saveQuestionsLocal(List<Question> remoteQuestions, int categoryId) {
        if (remoteQuestions == null) return;
        List<QuestionEntity> entities = new ArrayList<>();
        for (Question q : remoteQuestions) {
            entities.add(new QuestionEntity(q.id, categoryId, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_option, q.explanation, q.difficulty));
        }
        executor.execute(() -> {
            questionDao.deleteByCategory(categoryId);
            questionDao.insertAll(entities);
        });
    }
}
