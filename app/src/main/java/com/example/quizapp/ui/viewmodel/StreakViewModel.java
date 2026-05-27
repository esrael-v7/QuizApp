package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.StreakData;
import com.example.quizapp.data.repository.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StreakViewModel extends AndroidViewModel {
    private UserRepository repository;
    private MutableLiveData<StreakData> streakData = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public StreakViewModel(@NonNull Application application) {
        super(application);
        repository = new UserRepository(application);
    }

    public LiveData<StreakData> getStreakData() { return streakData; }
    public LiveData<String> getError() { return error; }

    public void fetchStreakData() {
        repository.getStreakData().enqueue(new Callback<GenericResponse<StreakData>>() {
            @Override
            public void onResponse(Call<GenericResponse<StreakData>> call, Response<GenericResponse<StreakData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    streakData.setValue(response.body().data);
                } else {
                    error.setValue("Failed to load streak data");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<StreakData>> call, Throwable t) {
                error.setValue("Connection failed");
            }
        });
    }
}
