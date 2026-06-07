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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = TokenManager.getInstance(this);
        
        // Debug check
        String currentRole = tokenManager.getRole();
        
        if (!tokenManager.isLoggedIn()) {
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
            return;
        }

        if (currentRole == null || !currentRole.equalsIgnoreCase("admin")) {
            Toast.makeText(this, "Security Check: Access Denied. Role is: " + currentRole, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, AdminLoginActivity.class));
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);




        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        binding.fab.setOnClickListener(v -> {
            showAddEditDialog(null);
        });

        binding.swipeRefresh.setOnRefreshListener(() -> fetchQuestions());

        binding.btnApplyFilter.setOnClickListener(v -> {
            fetchQuestions();
        });

        binding.btnClearFilter.setOnClickListener(v -> {
            binding.etSearch.setText("");
            binding.etFilterCategory.setText("");
            fetchQuestions();
        });


        fetchQuestions();
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



    private void fetchQuestions() {
        if (binding == null) return;
        String search = binding.etSearch.getText() != null ? binding.etSearch.getText().toString().trim() : "";
        String catIdStr = binding.etFilterCategory.getText() != null ? binding.etFilterCategory.getText().toString().trim() : "";
        Integer categoryId = null;
        if (!catIdStr.isEmpty()) {
            try {
                categoryId = Integer.parseInt(catIdStr);
            } catch (NumberFormatException ignored) {}
        }
        viewModel.fetchQuestions(1, categoryId, search.isEmpty() ? null : search);
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
                if (question.id == 0) {
                    Toast.makeText(AdminActivity.this, "Error: Question ID is 0, cannot edit.", Toast.LENGTH_SHORT).show();
                    return;
                }
                showAddEditDialog(question);
            }

            @Override
            public void onDelete(Question question) {
                if (question.id == 0) {
                    Toast.makeText(AdminActivity.this, "Error: Question ID is 0, cannot delete.", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete question #" + question.id + "?")
                        .setPositiveButton("Delete", (d, w) -> viewModel.deleteQuestion(question.id))
                        .setNegativeButton("Cancel", null)
                        .show();
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
            if (binding == null) return;
            binding.swipeRefresh.setRefreshing(false);
            if (questions == null || questions.isEmpty()) {
                Toast.makeText(this, "No questions found matching criteria", Toast.LENGTH_LONG).show();
                binding.tvQuestionCount.setText("No questions found");
            } else {
                binding.tvQuestionCount.setText("Showing " + questions.size() + " questions");
            }
            adapter.submitList(questions);
        });





        viewModel.getError().observe(this, error -> {
            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(error)
                    .setPositiveButton("OK", null)
                    .show();
        });



        viewModel.isLoading().observe(this, isLoading -> {
            if (binding == null) return;
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

    }
}


