package com.example.quizapp.data.repository;

import android.content.Context;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.HistoryResponse;
import com.example.quizapp.data.remote.model.ResultResponse;
import com.example.quizapp.data.remote.model.SessionAnswer;
import com.example.quizapp.data.remote.model.SubmitResultRequest;
import java.util.List;
import retrofit2.Call;

public class ResultRepository {
    private ApiService apiService;

    public ResultRepository(Context context) {
        this.apiService = RetrofitClient.getApi();
    }

    public Call<ResultResponse> submitResult(SubmitResultRequest request) {
        return apiService.submitDetailedResult(request);
    }

    public Call<HistoryResponse> getHistory(int page, int limit) {
        return apiService.getHistory(page, limit);
    }

    public Call<List<SessionAnswer>> getSessionDetail(int sessionId) {
        return apiService.getSessionDetail(sessionId);
    }
}
