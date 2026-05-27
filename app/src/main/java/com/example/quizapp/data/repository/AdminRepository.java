package com.example.quizapp.data.repository;

import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.Question;
import java.util.List;
import retrofit2.Call;

public class AdminRepository {
    private ApiService apiService;

    public AdminRepository() {
        this.apiService = RetrofitClient.getApi();
    }

    public Call<GenericResponse<List<Question>>> getAllQuestions(int page) {
        return apiService.getAllQuestions(page);
    }

    public Call<GenericResponse<Question>> addQuestion(Question question) {
        return apiService.addQuestion(question);
    }

    public Call<GenericResponse<Question>> updateQuestion(int id, Question question) {
        return apiService.updateQuestion(id, question);
    }

    public Call<GenericResponse<Void>> deleteQuestion(int id) {
        return apiService.deleteQuestion(id);
    }
}
