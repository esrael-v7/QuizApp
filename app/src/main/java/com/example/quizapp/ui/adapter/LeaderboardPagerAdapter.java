package com.example.quizapp.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.quizapp.ui.fragment.LeaderboardPageFragment;

public class LeaderboardPagerAdapter extends FragmentStateAdapter {

    public LeaderboardPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return LeaderboardPageFragment.newInstance("today");
            case 1: return LeaderboardPageFragment.newInstance("week");
            case 2: return LeaderboardPageFragment.newInstance("alltime");
            default: return LeaderboardPageFragment.newInstance("today");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
