package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.SubmitResultRequest;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;
import com.example.quizapp.data.repository.QuizRepository;

public class ResultsViewModel extends AndroidViewModel {
    private final QuizRepository repository;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ResultsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void submitResult(SubmitResultRequest request) {
        // Offline-first submission
        SubmitScoreRequest simpleRequest = new SubmitScoreRequest(request.category_id, request.correct_answers, request.total_questions);
        repository.submitScore(simpleRequest);
        
        // We could also implement a detailed sync, but for now we follow the user's 
        // requirement for offline-first score submission.
    }
}
