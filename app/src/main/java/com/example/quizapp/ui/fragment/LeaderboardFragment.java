package com.example.quizapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.quizapp.databinding.FragmentLeaderboardBinding;
import com.example.quizapp.ui.adapter.LeaderboardPagerAdapter;
import com.google.android.material.tabs.TabLayoutMediator;

public class LeaderboardFragment extends Fragment {
    private FragmentLeaderboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        LeaderboardPagerAdapter adapter = new LeaderboardPagerAdapter(this);
        binding.vpLeaderboard.setAdapter(adapter);

        new TabLayoutMediator(binding.tlPeriod, binding.vpLeaderboard, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Today"); break;
                case 1: tab.setText("This week"); break;
                case 2: tab.setText("All time"); break;
            }
        }).attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
