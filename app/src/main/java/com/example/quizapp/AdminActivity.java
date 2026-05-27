package com.example.quizapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.quizapp.data.remote.model.Question;
import com.example.quizapp.databinding.ActivityAdminBinding;
import com.example.quizapp.ui.adapter.AdminQuestionAdapter;
import com.example.quizapp.ui.fragment.AddEditQuestionDialogFragment;
import com.example.quizapp.ui.viewmodel.AdminViewModel;
import com.example.quizapp.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;


public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    private AdminViewModel viewModel;
    private AdminQuestionAdapter adapter;
    private TokenManager tokenManager;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = new TokenManager(this);
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        binding.fab.setOnClickListener(v -> {
            showAddEditDialog(null);
        });

        binding.btnNext.setOnClickListener(v -> {

            fetchQuestions(currentPage + 1);
        });

        binding.btnPrev.setOnClickListener(v -> {
            if (currentPage > 1) {
                fetchQuestions(currentPage - 1);
            }
        });

        fetchQuestions(currentPage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            showSignOutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sign out?")
                .setMessage("Are you sure you want to sign out from Admin panel?")
                .setPositiveButton("Sign out", (dialog, which) -> signOut())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void signOut() {
        tokenManager.clear();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        finish();
    }



    private void fetchQuestions(int page) {
        currentPage = page;
        viewModel.fetchQuestions(page);
    }


    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }


    private void setupRecyclerView() {
        adapter = new AdminQuestionAdapter(new AdminQuestionAdapter.OnQuestionActionListener() {
            @Override
            public void onEdit(Question question) {
                showAddEditDialog(question);
            }

            @Override
            public void onDelete(Question question) {
                viewModel.deleteQuestion(question.id);
            }
        });
        binding.rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvQuestions.setAdapter(adapter);
    }

    private void showAddEditDialog(Question question) {
        AddEditQuestionDialogFragment dialog = AddEditQuestionDialogFragment.newInstance(question);
        dialog.setOnQuestionSavedListener(q -> {
            if (q.id == 0) {
                viewModel.addQuestion(q);
            } else {
                viewModel.updateQuestion(q.id, q);
            }
        });
        dialog.show(getSupportFragmentManager(), "add_edit_question");
    }


    private void observeViewModel() {
        viewModel.getQuestions().observe(this, questions -> {
            if (questions == null || questions.isEmpty()) {
                Toast.makeText(this, "No questions found in backend", Toast.LENGTH_LONG).show();
            }
            adapter.submitList(questions);
        });


        viewModel.getError().observe(this, error -> {
            Snackbar.make(binding.getRoot(), error, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> fetchQuestions(currentPage))
                    .show();
        });


        viewModel.isLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
}


