package com.example.quizapp;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.quizapp.data.remote.model.CalendarDay;
import com.example.quizapp.data.remote.model.StreakData;
import com.example.quizapp.databinding.ActivityStreakBinding;
import com.example.quizapp.ui.viewmodel.StreakViewModel;
import com.google.android.material.snackbar.Snackbar;

public class StreakActivity extends AppCompatActivity {
    private ActivityStreakBinding binding;
    private StreakViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStreakBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(StreakViewModel.class);

        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        observeViewModel();
        viewModel.fetchStreakData();
    }

    private void observeViewModel() {
        viewModel.getStreakData().observe(this, this::updateUI);
        viewModel.getError().observe(this, error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }

    private void updateUI(StreakData data) {
        if (data == null) return;
        binding.tvStreakCount.setText(String.valueOf(data.current_streak));
        binding.tvBest.setText(String.valueOf(data.best_streak));
        binding.tvTotal.setText(String.valueOf(data.total_active_days));

        if (data.calendar_data != null) {
            setupCalendar(data.calendar_data);
        }
    }

    private void setupCalendar(java.util.List<CalendarDay> days) {
        binding.calendarGrid.removeAllViews();
        
        // Add headers
        String[] headers = {"M", "T", "W", "T", "F", "S", "S"};
        for (String h : headers) {
            TextView tv = new TextView(this);
            tv.setText(h);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(0xFF888888);
            tv.setTextSize(11);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (36 * getResources().getDisplayMetrics().density);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);
            binding.calendarGrid.addView(tv);
        }

        // Add days
        for (CalendarDay day : days) {
            TextView tv = new TextView(this);
            String[] dateParts = day.date.split("-");
            tv.setText(dateParts[dateParts.length - 1]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(11);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = (int) (36 * getResources().getDisplayMetrics().density);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tv.setLayoutParams(params);

            if ("done".equals(day.status)) {
                tv.setBackgroundResource(R.drawable.bg_circle);
                tv.getBackground().setTint(0xFFE8F5E9);
                tv.setTextColor(0xFF1B5E20);
            } else if ("missed".equals(day.status)) {
                tv.setBackgroundResource(R.drawable.bg_circle);
                tv.getBackground().setTint(0xFFFFEBEE);
                tv.setTextColor(0xFF7F0000);
            }
            
            binding.calendarGrid.addView(tv);
        }
    }
}
