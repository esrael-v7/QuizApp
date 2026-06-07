package com.example.quizapp.sync;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.quizapp.data.local.AppDatabase;
import com.example.quizapp.data.local.dao.SyncActionDao;
import com.example.quizapp.data.local.entity.SyncActionEntity;
import com.example.quizapp.data.remote.ApiService;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.SubmitScoreRequest;
import com.example.quizapp.data.remote.model.AddFavouriteRequest;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Response;

public class SyncWorker extends Worker {
    private final SyncActionDao syncActionDao;
    private final ApiService apiService;
    private final Gson gson;

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        AppDatabase db = AppDatabase.getInstance(context);
        syncActionDao = db.syncActionDao();
        apiService = RetrofitClient.getApi();
        gson = new Gson();
    }

    @NonNull
    @Override
    public Result doWork() {
        List<SyncActionEntity> pendingActions = syncActionDao.getPendingActions();
        android.util.Log.d("SYNC_DEBUG", "Found " + pendingActions.size() + " pending actions");
        
        boolean allSuccess = true;

        for (SyncActionEntity action : pendingActions) {
            boolean success = false;
            android.util.Log.d("SYNC_DEBUG", "Syncing action: " + action.actionType);
            try {
                switch (action.actionType) {
                    case "SUBMIT_SCORE":
                        SubmitScoreRequest scoreReq = gson.fromJson(action.dataJson, SubmitScoreRequest.class);
                        Response<com.example.quizapp.data.remote.model.GenericResponse<Void>> scoreRes = apiService.submitScore(scoreReq).execute();
                        if (scoreRes.isSuccessful()) {
                            success = true;
                            android.util.Log.d("SYNC_DEBUG", "Score submitted successfully");
                        }
                        break;
                    case "ADD_FAVOURITE":
                        AddFavouriteRequest favReq = gson.fromJson(action.dataJson, AddFavouriteRequest.class);
                        Response<com.example.quizapp.data.remote.model.GenericResponse<com.example.quizapp.data.remote.model.MessageResponse>> favRes = apiService.addFavourite(favReq).execute();
                        if (favRes.isSuccessful()) {
                            success = true;
                            android.util.Log.d("SYNC_DEBUG", "Favourite added successfully");
                        }
                        break;
                    case "REMOVE_FAVOURITE":
                        int questionId = Integer.parseInt(action.dataJson);
                        Response<com.example.quizapp.data.remote.model.GenericResponse<com.example.quizapp.data.remote.model.MessageResponse>> remRes = apiService.removeFavourite(questionId).execute();
                        if (remRes.isSuccessful()) {
                            success = true;
                            android.util.Log.d("SYNC_DEBUG", "Favourite removed successfully");
                        }
                        break;
                }

                if (success) {
                    syncActionDao.delete(action);
                } else {
                    allSuccess = false;
                    android.util.Log.e("SYNC_DEBUG", "Action failed to sync");
                }
            } catch (Exception e) {
                allSuccess = false;
                android.util.Log.e("SYNC_DEBUG", "Error during sync: " + e.getMessage());
            }
        }

        return allSuccess ? Result.success() : Result.retry();
    }
}
