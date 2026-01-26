package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView imgPoster;
    private TextView tvMovieId, tvTitle, tvDesc, tvLength, tvStaffId;
    private MovieService movieService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_details);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarMovieDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        imgPoster = findViewById(R.id.imgPoster);
        tvMovieId = findViewById(R.id.idmovie);
        tvTitle   = findViewById(R.id.tvtitle);
        tvDesc    = findViewById(R.id.tvdesc);
        tvLength  = findViewById(R.id.tvlength);
        tvStaffId = findViewById(R.id.idstaff);

        // Get movieCode from Intent
        int movieCode = getIntent().getIntExtra("movieCode", 0);
        if (movieCode == 0) {
            Toast.makeText(this, "No movie code passed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get API key from SharedPreferences
        SharedPrefManager spm = new SharedPrefManager(this);
        String apiKey = spm.getToken();

        movieService = ApiUtils.getMovieService();
        fetchMovieDetails(apiKey, movieCode);
    }

    private void fetchMovieDetails(String apiKey, int movieCode) {
        movieService.getMovie(apiKey, movieCode).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Log.d("MyApp", "Response: " + response.raw().toString());

                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    tvMovieId.setText(String.valueOf(movie.getId()));
                    tvTitle.setText(movie.getTitle());
                    tvDesc.setText(movie.getDescription());
                    tvLength.setText(movie.getLength() + " min");
                    tvStaffId.setText(String.valueOf(movie.getStaffID()));

                    // Load poster image
                    String imageUrl = ApiUtils.IMAGE_URL + movie.getImagePoster();
                    Glide.with(MovieDetailsActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imgPoster);

                } else if (response.code() == 401) {
                    Toast.makeText(MovieDetailsActivity.this, "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(MovieDetailsActivity.this, "Failed to load movie: " + response.code(), Toast.LENGTH_SHORT).show();
                    Log.e("MyApp", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("MyApp", t.toString());
            }
        });
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = new SharedPrefManager(this);
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
