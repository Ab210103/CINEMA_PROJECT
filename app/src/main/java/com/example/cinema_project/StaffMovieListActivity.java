package com.example.cinema_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.adapter.MovieAdapter;
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
    private MovieAdapter adapter;
    private MovieService movieService;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_staff_movie_list);

        rvMovieList = findViewById(R.id.rvMovieList);
        fab = findViewById(R.id.fab);

        movieService = ApiUtils.getMovieService();

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, NewMovieActivity.class));
        });

        // register context menu
        registerForContextMenu(rvMovieList);

        loadMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies();
    }

    /**
     * Load movie list from API
     */
    private void loadMovies() {

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        String token = spm.getToken();

        movieService.getAllMovie(token).enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

                Log.d("MyApp:", "Movie List Response: " + response.raw());

                if (response.code() == 200) {

                    List<Movie> movies = response.body();

                    adapter = new MovieAdapter(
                            getApplicationContext(),
                            movies,
                            false,
                            R.layout.item_list_staff_movie,
                            R.id.imgMovie,
                            R.id.tvMovieValue,
                            0,
                            null
                    );

                    rvMovieList.setAdapter(adapter);
                    rvMovieList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    DividerItemDecoration divider =
                            new DividerItemDecoration(rvMovieList.getContext(),
                                    DividerItemDecoration.VERTICAL);
                    rvMovieList.addItemDecoration(divider);

                } else if (response.code() == 401) {

                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        "Error connecting to server",
                        Toast.LENGTH_LONG).show();
                Log.e("MyApp:", t.toString());
            }
        });
    }

    /**
     * Context menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Movie selectedMovie = adapter.getSelectedItem();
        Log.d("MyApp:", "Selected movie: " + selectedMovie.toString());

        if (item.getItemId() == R.id.menu_details) {
            viewDetails(selectedMovie);
        }
        else if (item.getItemId() == R.id.menu_update) {
            updateMovie(selectedMovie);
        }
        else if (item.getItemId() == R.id.menu_delete) {
            confirmDelete(selectedMovie);
        }

        return super.onContextItemSelected(item);
    }

    private void viewDetails(Movie movie) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movieCode", movie.getId());
        startActivity(intent);
    }

    private void updateMovie(Movie movie) {
        Intent intent = new Intent(this, UpdateMovieActivity.class);
        intent.putExtra("movieCode", movie.getId());
        startActivity(intent);
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
        String token = spm.getToken();

        movieService.deleteMovie(token, movie.getId())
                .enqueue(new Callback<DeleteResponse>() {
                    @Override
                    public void onResponse(Call<DeleteResponse> call,
                                           Response<DeleteResponse> response) {

                        if (response.code() == 200) {
                            displayAlert("Movie deleted successfully");
                            loadMovies();
                        }
                        else if (response.code() == 401) {
                            Toast.makeText(getApplicationContext(),
                                    "Invalid session. Please login again",
                                    Toast.LENGTH_LONG).show();
                            clearSessionAndRedirect();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + response.message(),
                                    Toast.LENGTH_LONG).show();
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
}
