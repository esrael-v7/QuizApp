package com.example.quizapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.data.repository.QuizRepository;
import com.example.quizapp.utils.NetworkUtils;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesViewModel extends AndroidViewModel {
    private final QuizRepository repository;
    private final LiveData<List<Question>> favourites;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        repository = new QuizRepository(application);
        // Observe local database directly
        favourites = repository.getFavouritesLocal();
    }

    public LiveData<List<Question>> getFavourites() { return favourites; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> isLoading() { return loading; }

    private long lastFetchTime = 0;

    public void fetchFavourites() {
        // Prevent spamming requests (Rate limit 30s)
        if (System.currentTimeMillis() - lastFetchTime < 30000) {
            return;
        }
        
        // Only fetch if online
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            return;
        }

        loading.setValue(true);
        lastFetchTime = System.currentTimeMillis();
        repository.getFavouritesRemote().enqueue(new Callback<GenericResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Question>>> call, Response<GenericResponse<List<Question>>> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    repository.saveFavouritesLocal(response.body().data);
                } else if (response.code() == 429) {
                    error.setValue("Too many requests. Please wait.");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Question>>> call, Throwable t) {
                loading.setValue(false);
                // Silent failure if we have local data
            }
        });
    }

    public void removeFavourite(int questionId) {
        // Offline-first: schedule background removal
        repository.removeFavourite(questionId);
        
        // The UI will update automatically because we are observing FavouritesLocal LiveData
        // (Assuming you update the local DB in the removeFavourite repository method if you want instant feedback)
    }
}
