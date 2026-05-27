package com.example.quizapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.quizapp.R;
import com.example.quizapp.databinding.FragmentProgressBinding;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quizapp.data.remote.model.UserStats;
import com.example.quizapp.ui.adapter.CategoryScoreAdapter;
import com.example.quizapp.ui.viewmodel.ProgressViewModel;
import com.google.android.material.snackbar.Snackbar;

public class ProgressFragment extends Fragment {
    private FragmentProgressBinding binding;
    private ProgressViewModel viewModel;
    private CategoryScoreAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProgressViewModel.class);

        setupRecyclerView();

        observeViewModel();
        viewModel.fetchStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.fetchStats();
        }
    }


    private void setupRecyclerView() {
        adapter = new CategoryScoreAdapter();
        binding.rvCategoryScores.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCategoryScores.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getStats().observe(getViewLifecycleOwner(), this::updateUI);

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }

    private void updateUI(UserStats stats) {
        if (stats == null) return;
        binding.tvTotalQuizzes.setText(String.valueOf(stats.total_quizzes));
        binding.tvAvgScore.setText(String.format("%.0f%%", stats.avg_score));
        binding.tvStreak.setText(String.valueOf(stats.current_streak));
        binding.tvTotalPoints.setText(String.format("%,d", stats.total_points));

        if (stats.scores_by_category != null) {
            adapter.submitList(stats.scores_by_category);
        }

        if (stats.weak_areas != null && !stats.weak_areas.isEmpty()) {
            binding.cgWeakAreas.removeAllViews();
            binding.tvNoWeakAreas.setVisibility(View.GONE);
            binding.cgWeakAreas.setVisibility(View.VISIBLE);
            for (String area : stats.weak_areas) {
                com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(requireContext());
                chip.setText(area);
                chip.setChipBackgroundColorResource(R.color.colorErrorLight);
                chip.setTextColor(getResources().getColor(R.color.colorErrorDark));
                binding.cgWeakAreas.addView(chip);
            }
        } else {
            binding.cgWeakAreas.setVisibility(View.GONE);
            binding.tvNoWeakAreas.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
