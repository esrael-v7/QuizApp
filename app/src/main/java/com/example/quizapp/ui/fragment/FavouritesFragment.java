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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.FragmentFavouritesBinding;
import com.example.quizapp.ui.adapter.FavouriteAdapter;
import com.example.quizapp.ui.viewmodel.FavouritesViewModel;
import com.example.quizapp.QuizActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouritesFragment extends Fragment {
    private FragmentFavouritesBinding binding;
    private FavouritesViewModel viewModel;
    private FavouriteAdapter adapter;
    private List<Question> allFavourites = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        setupRecyclerView();
        setupFilter();
        observeViewModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchFavourites();
    }

    private void setupRecyclerView() {
        adapter = new FavouriteAdapter(new FavouriteAdapter.OnFavouriteClickListener() {
            @Override
            public void onRemoveClick(Question question) {
                viewModel.removeFavourite(question.id);
            }

            @Override
            public void onPracticeClick(Question question) {
                if (getActivity() == null) return;
                
                Intent intent = new Intent(getActivity(), QuizActivity.class);
                intent.putExtra("category_name", "Practice Mode");
                // Pass a list containing only this question
                List<Question> list = Collections.singletonList(question);
                intent.putExtra("questions_json", new Gson().toJson(list));
                startActivity(intent);
            }
        });
        binding.rvFavourites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFavourites.setAdapter(adapter);
    }

    private void setupFilter() {
        binding.cgFilter.setOnCheckedChangeListener((group, checkedId) -> {
            filterList();
        });
    }

    private void filterList() {
        int checkedId = binding.cgFilter.getCheckedChipId();
        if (checkedId == binding.chipAll.getId()) {
            adapter.submitList(allFavourites);
        } else {
            // Logic to filter by category if category name was available in Question model
            // For now, just showing all
            adapter.submitList(allFavourites);
        }
        updateEmptyState(adapter.getItemCount() == 0);
    }

    private void observeViewModel() {
        viewModel.getFavourites().observe(getViewLifecycleOwner(), list -> {
            allFavourites = list;
            filterList();
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_LONG).show();
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        binding.tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.rvFavourites.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
