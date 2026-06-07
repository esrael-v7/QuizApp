package com.example.quizapp.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quizapp.data.remote.model.LeaderboardEntry;
import com.example.quizapp.databinding.FragmentLeaderboardPageBinding;
import com.example.quizapp.ui.adapter.RankedUserAdapter;
import com.example.quizapp.ui.viewmodel.LeaderboardViewModel;
import com.example.quizapp.utils.TokenManager;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardPageFragment extends Fragment {
    private static final String ARG_PERIOD = "period";
    private String period;
    private FragmentLeaderboardPageBinding binding;
    private LeaderboardViewModel viewModel;
    private RankedUserAdapter adapter;

    public static LeaderboardPageFragment newInstance(String period) {
        LeaderboardPageFragment fragment = new LeaderboardPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PERIOD, period);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            period = getArguments().getString(ARG_PERIOD);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLeaderboardPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(LeaderboardViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.swipeRefresh.setOnRefreshListener(this::fetchData);
        fetchData();
    }

    private void setupRecyclerView() {
        adapter = new RankedUserAdapter();
        binding.rvRankedList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRankedList.setAdapter(adapter);
    }

    private void fetchData() {
        viewModel.fetchLeaderboard(period);
    }

    private void observeViewModel() {
        viewModel.getLeaderboard().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefresh.setRefreshing(false);
            if (response != null) {
                updateUI(response);
                
                // Highlight current user
                int currentUserId = TokenManager.getInstance(requireContext()).getUserId();
                boolean foundMe = false;

                for (int i = 0; i < response.size(); i++) {
                    LeaderboardEntry entry = response.get(i);
                    if (entry.user_id == currentUserId) {
                        binding.tvMyRank.setText("#" + (i + 1));
                        binding.tvMyInfo.setText("You · " + (entry.best_category != null ? entry.best_category : "Top Player"));
                        binding.tvMyPoints.setText((entry.score != null ? entry.score : "0") + " pts");
                        foundMe = true;
                        break;
                    }

                }
                
                // If not in the list, hide the highlight bar or show generic
                if (!foundMe) {
                    binding.tvMyRank.setText("-");
                    binding.tvMyInfo.setText("You are not ranked today");
                    binding.tvMyPoints.setText("0 pts");
                }
            }
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!binding.swipeRefresh.isRefreshing()) {
                // Show other loading indicator if needed
            }
        });
    }

    private void updateUI(List<LeaderboardEntry> data) {
        // Reset podium first regardless of data
        resetPodium();
        
        if (data == null || data.isEmpty()) {
            adapter.submitList(new ArrayList<>());
            binding.rvRankedList.setVisibility(View.GONE);
            return;
        }

        List<LeaderboardEntry> list = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            LeaderboardEntry entry = data.get(i);
            
            // Use position in list as the display rank for this period
            int displayRank = i + 1;
            entry.rank = displayRank; // Update the entry rank for the list display
            
            String initials = getInitials(entry.full_name);
            String scoreText = (entry.score != null ? entry.score : "0") + " pts";

            if (displayRank == 1) {
                binding.tvName1.setText(entry.full_name != null ? entry.full_name : "-");
                binding.tvInitials1.setText(initials);
                binding.tvScore1.setText(scoreText);
            } else if (displayRank == 2) {
                binding.tvName2.setText(entry.full_name != null ? entry.full_name : "-");
                binding.tvInitials2.setText(initials);
                binding.tvScore2.setText(scoreText);
            } else if (displayRank == 3) {
                binding.tvName3.setText(entry.full_name != null ? entry.full_name : "-");
                binding.tvInitials3.setText(initials);
                binding.tvScore3.setText(scoreText);
            } else {
                list.add(entry);
            }
        }

        // Fix: Always ensure the list is set even if only 1 item (Rank 4) exists
        adapter.submitList(new ArrayList<>(list)); 
        binding.rvRankedList.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
        
        // Force the parent to re-layout so the RecyclerView gets its correct size
        binding.getRoot().post(() -> {
            if (binding != null) {
                binding.rvRankedList.requestLayout();
            }
        });
    }




    private void resetPodium() {
        binding.tvName1.setText("-"); binding.tvInitials1.setText(""); binding.tvScore1.setText("0 pts");
        binding.tvName2.setText("-"); binding.tvInitials2.setText(""); binding.tvScore2.setText("0 pts");
        binding.tvName3.setText("-"); binding.tvInitials3.setText(""); binding.tvScore3.setText("0 pts");
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].toUpperCase().charAt(0));
            }
        }
        return initials.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
