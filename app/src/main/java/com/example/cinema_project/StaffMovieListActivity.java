package com.example.cinema_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.adapter.ListAdapter;
import com.example.cinema_project.model.DeleteResponse;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffMovieListActivity extends AppCompatActivity {

    private RecyclerView rvMovieList;
    private ListAdapter adapter;
    private MovieService movieService;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_movie_list);

        // Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarMovieList);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rvMovieList = findViewById(R.id.rvMovieList);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(this, NewMovieActivity.class)));

        movieService = ApiUtils.getMovieService();
        registerForContextMenu(rvMovieList);

        updateRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    /** Fetch movie list and update RecyclerView */
    private void updateRecyclerView() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String apiKey = spm.getToken();

        movieService.getAllMovie().enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                Log.d("StaffMovieList", "Response: " + response.raw());

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();

                    adapter = new ListAdapter(getApplicationContext(), movies);
                    rvMovieList.setAdapter(adapter);
                    rvMovieList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    DividerItemDecoration divider = new DividerItemDecoration(rvMovieList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    rvMovieList.addItemDecoration(divider);
                    registerForContextMenu(rvMovieList);

                } else if (response.code() == 401) {
                    Toast.makeText(getApplicationContext(), "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getApplicationContext(), "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Error connecting to server", Toast.LENGTH_LONG).show();
                Log.e("StaffMovieList", t.toString());
            }
        });
    }

    /** Context menu */
    @Override
    public void onCreateContextMenu(ContextMenu menu, android.view.View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (adapter == null) return super.onContextItemSelected(item);

        Movie selectedMovie = adapter.getSelectedItem();
        if (selectedMovie == null) return super.onContextItemSelected(item);

        int id = item.getItemId();
        if (id == R.id.menu_details) {
            viewDetails(selectedMovie);
        } else if (id == R.id.menu_update) {
            updateMovie(selectedMovie);
        } else if (id == R.id.menu_delete) {
            confirmDelete(selectedMovie);
        }

        return super.onContextItemSelected(item);
    }

    private void viewDetails(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieCode", movie.getId());
        startActivity(intent);
        adapter.clearSelection();
    }

    private void updateMovie(Movie movie) {
        Intent intent = new Intent(this, UpdateMovieActivity.class);
        intent.putExtra("movieCode", movie.getId());
        startActivity(intent);
        adapter.clearSelection();
    }

    private void confirmDelete(Movie movie) {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMovie(movie))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMovie(Movie movie) {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String apiKey = spm.getToken();

        movieService.deleteMovie(apiKey, movie.getId())
                .enqueue(new Callback<DeleteResponse>() {
                    @Override
                    public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                        if (response.isSuccessful()) {
                            displayAlert("Movie deleted successfully");
                            updateRecyclerView();
                            if (adapter != null) adapter.clearSelection();
                        } else if (response.code() == 401) {
                            Toast.makeText(getApplicationContext(),
                                    "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                            clearSessionAndRedirect();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + response.message(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteResponse> call, Throwable t) {
                        displayAlert("Error: " + t.getMessage());
                    }
                });
    }

    private void displayAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
