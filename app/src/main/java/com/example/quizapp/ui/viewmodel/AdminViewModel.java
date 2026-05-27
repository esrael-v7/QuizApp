package com.example.quizapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.repository.AdminRepository;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends ViewModel {
    private AdminRepository repository;
    private MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public AdminViewModel() {
        this.repository = new AdminRepository();
    }

    public LiveData<List<Question>> getQuestions() { return questions; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    public void fetchQuestions(int page) {
        loading.setValue(true);
        repository.getAllQuestions(page).enqueue(new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    questions.setValue(response.body().data);
                } else {
                    String url = call.request().url().toString();
                    error.setValue("Error " + response.code() + " at: " + url);
                }
            }


            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed: " + t.getMessage());
            }

        });
    }

    public void deleteQuestion(int id) {
        repository.deleteQuestion(id).enqueue(new Callback<GenericResponse<Void>>() {
            @Override
            public void onResponse(Call<GenericResponse<Void>> call, Response<GenericResponse<Void>> response) {
                if (response.isSuccessful()) {
                    fetchQuestions(1); // Refresh
                } else {
                    error.setValue("Failed to delete");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Void>> call, Throwable t) {
                error.setValue("Connection failed");
            }
        });
    }

    public void addQuestion(Question question) {
        loading.setValue(true);
        repository.addQuestion(question).enqueue(new Callback<GenericResponse<Question>>() {
            @Override
            public void onResponse(Call<GenericResponse<Question>> call, Response<GenericResponse<Question>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    fetchQuestions(1);
                } else {
                    error.setValue("Failed to add question");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Question>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }

    public void updateQuestion(int id, Question question) {
        loading.setValue(true);
        repository.updateQuestion(id, question).enqueue(new Callback<GenericResponse<Question>>() {
            @Override
            public void onResponse(Call<GenericResponse<Question>> call, Response<GenericResponse<Question>> response) {
                loading.setValue(false);
                if (response.isSuccessful()) {
                    fetchQuestions(1);
                } else {
                    error.setValue("Failed to update question");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Question>> call, Throwable t) {
                loading.setValue(false);
                error.setValue("Connection failed");
            }
        });
    }
}

