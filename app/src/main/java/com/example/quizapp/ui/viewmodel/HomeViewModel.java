package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.HistoryResponse;
import com.example.quizapp.data.remote.model.QuizHistory;
import com.example.quizapp.data.repository.QuizRepository;
import com.example.quizapp.data.repository.ResultRepository;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private QuizRepository quizRepository;
    private ResultRepository resultRepository;
    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<List<QuizHistory>> recentActivity = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.quizRepository = new QuizRepository(application);
        this.resultRepository = new ResultRepository(application);
    }

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<QuizHistory>> getRecentActivity() { return recentActivity; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void fetchCategories() {
        if (categories.getValue() != null && !categories.getValue().isEmpty()) {
            return; // Use cached data for speed
        }
        loading.setValue(true);
        quizRepository.getCategories().enqueue(new Callback<GenericResponse<List<Category>>>() {

            @Override
            public void onResponse(Call<GenericResponse<List<Category>>> call, Response<GenericResponse<List<Category>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    categories.setValue(response.body().data);
                } else {
                    error.setValue("Failed to load categories");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Category>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }

    public void fetchRecentActivity() {
        if (recentActivity.getValue() != null && !recentActivity.getValue().isEmpty()) {
            return;
        }
        resultRepository.getHistory(1, 5).enqueue(new Callback<HistoryResponse>() {

            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recentActivity.setValue(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                // Ignore failure for recent activity
            }
        });
    }
}
