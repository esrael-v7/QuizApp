package com.example.quizapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quizapp.QuizActivity;
import com.example.quizapp.R;
import com.example.quizapp.databinding.FragmentHomeBinding;

import com.example.quizapp.ui.adapter.CategoryAdapter;
import com.example.quizapp.ui.adapter.RecentQuizAdapter;
import com.example.quizapp.ui.viewmodel.HomeViewModel;
import com.example.quizapp.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private RecentQuizAdapter recentAdapter;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        tokenManager = new TokenManager(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        
        binding.tvGreeting.setText("Hello, " + tokenManager.getFullName() + "!");

        
        setupRecyclerView();
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Caching is handled in ViewModel
        viewModel.fetchCategories();
        viewModel.fetchRecentActivity();
    }


    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(category -> {
            Intent intent = new Intent(requireContext(), QuizActivity.class);
            intent.putExtra("category_id", category.id);
            intent.putExtra("category_name", category.name);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
            }
        });

        binding.rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvCategories.setAdapter(categoryAdapter);

        recentAdapter = new RecentQuizAdapter(item -> {
            // startActivity(new Intent(requireContext(), ResultDetailActivity.class).putExtra("session_id", item.session_id));
        });
        binding.rvRecent.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecent.setAdapter(recentAdapter);
    }

    private void observeViewModel() {
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryAdapter.submitList(categories);
        });

        viewModel.getRecentActivity().observe(getViewLifecycleOwner(), activities -> {
            recentAdapter.submitList(activities);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
