package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.MessageResponse;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.repository.QuizRepository;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizViewModel extends AndroidViewModel {
    private QuizRepository repository;
    private MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<List<Question>> getQuestions() { return questions; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void fetchQuestions(int categoryId) {
        loading.setValue(true);
        repository.getQuestions(categoryId, 1, 20).enqueue(new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    List<Question> list = response.body().data;
                    Collections.shuffle(list, new Random());
                    questions.setValue(list);
                } else {
                    error.setValue("Failed to load questions");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }

    public void addFavourite(int questionId) {
        repository.addFavourite(questionId).enqueue(new Callback<GenericResponse<MessageResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<MessageResponse>> call, Response<GenericResponse<MessageResponse>> response) {
                if (!response.isSuccessful()) {
                    error.setValue("Failed to save favourite");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<MessageResponse>> call, Throwable t) {
                error.setValue("Connection failed");
            }
        });
    }
}
