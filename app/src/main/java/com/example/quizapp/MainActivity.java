package com.example.quizapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.quizapp.databinding.ActivityMainBinding;
import com.example.quizapp.utils.TokenManager;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import androidx.navigation.NavOptions;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private TokenManager tokenManager;
    private SharedPreferences settingsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = new TokenManager(this);
        settingsPrefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        updateSyncStatusIndicator();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        
        if (navHostFragment == null) return;
        
        NavController navController = navHostFragment.getNavController();

        binding.navView.setOnItemSelectedListener(item -> {
            if (navController.getCurrentDestination() != null && 
                item.getItemId() == navController.getCurrentDestination().getId()) {
                return false;
            }

            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), false, true)
                    .setEnterAnim(R.anim.nav_fade_in)
                    .setExitAnim(R.anim.nav_fade_out)
                    .setPopEnterAnim(R.anim.nav_fade_in)
                    .setPopExitAnim(R.anim.nav_fade_out)
                    .build();

            try {
                navController.navigate(item.getItemId(), null, navOptions);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            android.view.MenuItem menuItem = binding.navView.getMenu().findItem(destination.getId());
            if (menuItem != null) {
                menuItem.setChecked(true);
            }
        });


    }



    public void updateSyncStatusIndicator() {
        boolean isSyncEnabled = settingsPrefs.getBoolean("cloud_sync", true);
        boolean isLoggedIn = tokenManager.isLoggedIn();

        if (isSyncEnabled && isLoggedIn) {
            binding.tvSyncStatusIndicator.setText(R.string.cloud_sync_active);
            binding.tvSyncStatusIndicator.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        } else {
            binding.tvSyncStatusIndicator.setText(R.string.local_mode);
            binding.tvSyncStatusIndicator.setBackgroundColor(Color.parseColor("#757575")); // Grey
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSyncStatusIndicator();
    }
}

