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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesViewModel extends AndroidViewModel {
    private QuizRepository repository;
    private MutableLiveData<List<Question>> favourites = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repository = new QuizRepository(application);
    }

    public LiveData<List<Question>> getFavourites() { return favourites; }
    public LiveData<String> getError() { return error; }

    public void fetchFavourites() {
        repository.getFavourites().enqueue(new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().data != null) {
                        favourites.setValue(response.body().data);
                    } else {
                        favourites.setValue(new java.util.ArrayList<>());
                    }
                } else if (response.code() == 404 || response.code() == 204) {
                    favourites.setValue(new java.util.ArrayList<>());
                } else {
                    // Only show error if it's actually a failure, not just empty
                    error.setValue("Failed to load favourites");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                error.setValue("Connection failed");
            }
        });
    }

    public void removeFavourite(int questionId) {
        repository.removeFavourite(questionId).enqueue(new Callback<GenericResponse<MessageResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<MessageResponse>> call, Response<GenericResponse<MessageResponse>> response) {
                if (response.isSuccessful()) {
                    fetchFavourites();
                } else {
                    error.setValue("Failed to remove favourite");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<MessageResponse>> call, Throwable t) {
                error.setValue("Connection failed");
            }
        });
    }
}
