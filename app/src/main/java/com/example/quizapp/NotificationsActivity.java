package com.example.quizapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quizapp.data.remote.RetrofitClient;
import com.example.quizapp.data.remote.model.GenericResponse;
import com.example.quizapp.data.remote.model.Notification;
import com.example.quizapp.databinding.ActivityNotificationsBinding;
import com.example.quizapp.ui.adapter.NotificationAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {
    private ActivityNotificationsBinding binding;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        setupRecyclerView();
        fetchNotifications();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(notification -> {
            // Mark as read logic
        });
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void fetchNotifications() {
        RetrofitClient.getApi().getNotifications(1).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Notification>>> call, Response<GenericResponse<List<Notification>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    adapter.submitList(response.body().data);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Notification>>> call, Throwable t) {
            }
        });
    }
}
