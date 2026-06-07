package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;
import com.example.quizapp.data.repository.QuizRepository;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizViewModel extends AndroidViewModel {
    private final QuizRepository repository;
    private final MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<List<Question>> getQuestions() { return questions; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void loadQuestions(int categoryId) {
        loading.setValue(true);
        repository.getQuestions(categoryId, 20, data -> {
            loading.postValue(false);
            if (data != null && !data.isEmpty()) {
                Collections.shuffle(data, new Random());
                questions.postValue(data);
            } else {
                error.postValue("No questions found.");
            }
        });
    }

    public void submitScore(int categoryId, int score, int total) {
        repository.submitScore(new SubmitScoreRequest(categoryId, score, total));
    }

    public void addFavourite(int questionId) {
        repository.addFavourite(questionId);
    }

    public void removeFavourite(int questionId) {
        repository.removeFavourite(questionId);
    }
}
