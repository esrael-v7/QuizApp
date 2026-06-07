package com.example.quizapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.quizapp.LoginActivity;
import com.example.quizapp.MainActivity;
import com.example.quizapp.R;
import com.example.quizapp.databinding.FragmentProfileBinding;


import com.example.quizapp.ui.viewmodel.ProgressViewModel;
import com.example.quizapp.utils.TokenManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TokenManager tokenManager;
    private SharedPreferences settingsPrefs;
    private ProgressViewModel progressViewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        tokenManager = TokenManager.getInstance(requireContext());

        settingsPrefs = requireContext()
                .getSharedPreferences("settings", Context.MODE_PRIVATE);


        progressViewModel = new ViewModelProvider(requireActivity())
                .get(ProgressViewModel.class);

        binding.tvName.setText(tokenManager.getFullName());


        binding.tvEmail.setText(
                tokenManager.isLoggedIn()
                        ? "Logged in"
                        : "Not logged in"
        );

        setupDarkMode();

        setupDataSync();

        observeStats();

        progressViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), error, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show();
            }
        });

        binding.btnSignOut.setOnClickListener(v -> showSignOutDialog());


        progressViewModel.fetchStats();
    }

    private void observeStats() {

        progressViewModel.getStats().observe(
                getViewLifecycleOwner(),
                stats -> {

                    if (stats != null) {

                        binding.tvStatQuizzes.setText(
                                String.valueOf(stats.total_quizzes)
                        );

                        binding.tvStatStreak.setText(
                                String.valueOf(stats.current_streak)
                        );

                        binding.tvStatScore.setText(
                                String.format(
                                        java.util.Locale.getDefault(),
                                        "%.0f%%",
                                        stats.avg_score
                                )
                        );

                        binding.tvLevel.setText(
                                "Level " +
                                        (stats.user_level > 0
                                                ? stats.user_level
                                                : 1)
                        );

                        if (stats.last_synced_at != null &&
                                !stats.last_synced_at.isEmpty()) {

                            binding.tvLastSyncInfo.setText(
                                    "Last synced: " + stats.last_synced_at
                            );
                        }
                    }
                }
        );

        progressViewModel.isLoading().observe(
                getViewLifecycleOwner(),
                isLoading -> {

                    if (isLoading) {

                        binding.tvLastSyncInfo.setText("Syncing...");
                    }
                }
        );
    }

    private void setupDarkMode() {

        boolean isDark =
                settingsPrefs.getBoolean("dark_mode", false);

        binding.switchDarkMode.setChecked(isDark);

        updateDarkModeStatusText(isDark);

        binding.switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    int newMode =
                            isChecked
                                    ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO;

                    if (AppCompatDelegate.getDefaultNightMode()
                            == newMode) {
                        return;
                    }

                    settingsPrefs.edit()
                            .putBoolean("dark_mode", isChecked)
                            .apply();

                    updateDarkModeStatusText(isChecked);

                    // Force an instant activity recreation without flicker
                    if (getActivity() != null) {
                        getActivity().getWindow().setWindowAnimations(0);
                    }

                    // Fast smooth theme switching
                    AppCompatDelegate.setDefaultNightMode(newMode);
                }
        );
    }

    private void updateDarkModeStatusText(boolean isEnabled) {

        binding.tvDarkModeStatus.setText(
                isEnabled
                        ? "Enabled"
                        : "Disabled"
        );
    }

    private void setupDataSync() {
        boolean isSyncEnabled = settingsPrefs.getBoolean("cloud_sync", true);
        binding.switchCloudSync.setChecked(isSyncEnabled);
        updateSyncStatusText(isSyncEnabled);

        binding.switchCloudSync.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !tokenManager.isLoggedIn()) {
                buttonView.setChecked(false);
                showLoginRequiredDialog();
                return;
            }

            settingsPrefs.edit()
                    .putBoolean("cloud_sync", isChecked)
                    .apply();

            updateSyncStatusText(isChecked);

            if (isChecked && tokenManager.isLoggedIn()) {
                progressViewModel.syncData();
            }

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateSyncStatusIndicator();
            }
        });

        binding.layoutSyncInfo.setOnClickListener(v -> {
            if (binding.switchCloudSync.isChecked()) {
                if (tokenManager.isLoggedIn()) {
                    if (!com.example.quizapp.utils.NetworkUtils.isNetworkAvailable(requireContext())) {
                        com.google.android.material.snackbar.Snackbar.make(binding.getRoot(), "No internet connection.", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    progressViewModel.syncData();
                } else {
                    showLoginRequiredDialog();
                }
            }
        });
    }

    private void showLoginRequiredDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("You need to be logged in to sync your data to the cloud.")
                .setPositiveButton("Login", (dialog, which) -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void updateSyncStatusText(boolean isEnabled) {

        binding.tvSyncStatus.setText(
                isEnabled
                        ? "Quiz data syncs to cloud when online"
                        : "Data saved locally only"
        );


        if (!tokenManager.isLoggedIn()) {
            binding.tvLastSyncInfo.setText(
                    "Login required for cloud sync"
            );
        } else if (!isEnabled) {
            binding.tvLastSyncInfo.setText(
                    "Cloud sync is disabled"
            );
        } else {
            binding.tvLastSyncInfo.setText(
                    "Cloud sync is active"
            );
        }
    }



    private void showSignOutDialog() {

        new AlertDialog.Builder(requireContext())
                .setTitle("Sign out?")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton(
                        "Sign out",
                        (dialog, which) -> signOut()
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void signOut() {

        tokenManager.clear();

        Intent intent =
                new Intent(requireContext(), LoginActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        }
    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();

        binding = null;
    }
}
