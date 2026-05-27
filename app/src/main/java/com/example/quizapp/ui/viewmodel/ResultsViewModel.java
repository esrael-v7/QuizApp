package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.ResultResponse;
import com.example.quizapp.data.remote.model.SubmitResultRequest;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;
import com.example.quizapp.data.repository.QuizRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsViewModel extends AndroidViewModel {
    private QuizRepository repository;
    private MutableLiveData<ResultResponse> resultResponse = new MutableLiveData<>();
    private MutableLiveData<Boolean> submitSuccess = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public ResultsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<ResultResponse> getResultResponse() { return resultResponse; }
    public LiveData<Boolean> getSubmitSuccess() { return submitSuccess; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void submitResult(SubmitResultRequest request) {
        loading.setValue(true);
        
        // 1. Submit simple score (Guaranteed to be on backend)
        SubmitScoreRequest simpleRequest = new SubmitScoreRequest(request.category_id, request.correct_answers, request.total_questions);
        repository.submitScore(simpleRequest).enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                if (response.isSuccessful()) {
                    submitSuccess.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                // Fail silently for simple score
            }
        });

        // 2. Submit detailed result (For weak areas, history, etc.)
        repository.submitResult(request).enqueue(new Callback<ResultResponse>() {
            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    resultResponse.setValue(response.body());
                } else {
                    // It's possible only the simple submitScore is implemented on backend
                    // So we don't treat this as a total failure
                }
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Network sync issue");
            }
        });
    }
}
