package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.Category;
import com.example.quizapp.data.remote.model.HistoryResponse;
import com.example.quizapp.data.remote.model.QuizHistory;
import com.example.quizapp.data.repository.QuizRepository;
import com.example.quizapp.data.repository.ResultRepository;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final QuizRepository quizRepository;
    private final ResultRepository resultRepository;
    private final LiveData<List<Category>> categories;
    private final MutableLiveData<List<QuizHistory>> recentActivity = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.quizRepository = new QuizRepository(application);
        this.resultRepository = new ResultRepository(application);
        this.categories = quizRepository.getCategories();
    }

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<QuizHistory>> getRecentActivity() { return recentActivity; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void fetchCategories() {
        // Trigger a refresh from the repository
        quizRepository.getCategories();
    }

    public void fetchRecentActivity() {
        if (!com.example.quizapp.utils.NetworkUtils.isNetworkAvailable(getApplication())) {
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
            public void onFailure(Call<HistoryResponse> call, Throwable t) {}
        });
    }
}
