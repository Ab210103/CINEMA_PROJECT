package com.example.cinema_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private MovieService movieService;
    private ImageView imgPoster;
    private TextView tvMovieId, tvTitle, tvDesc, tvLength, tvStaffId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Toolbar toolbar = findViewById(R.id.toolbarMovieDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        imgPoster = findViewById(R.id.imgPoster);
        tvMovieId = findViewById(R.id.idmovie);
        tvTitle = findViewById(R.id.tvtitle);
        tvDesc = findViewById(R.id.tvdesc);
        tvLength = findViewById(R.id.tvlength);
        tvStaffId = findViewById(R.id.idstaff);

        int movieCode = getIntent().getIntExtra("movieCode", -1);
        if (movieCode == -1) {
            Toast.makeText(this, "No movie code passed", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager spm = new SharedPrefManager(this);
        String apiKey = spm.getToken();

        if (apiKey == null || apiKey.isEmpty()) {
            Toast.makeText(this, "API token missing", Toast.LENGTH_SHORT).show();
            return;
        }

        movieService = ApiUtils.getMovieService();
        fetchMovieDetails(apiKey, movieCode);
    }

    private void fetchMovieDetails(String apiKey, int movieCode) {
        movieService.getMovie(apiKey, movieCode).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Log.d("MovieDetails", "Response: " + response.raw());

                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    tvMovieId.setText(String.valueOf(movie.getId()));
                    tvTitle.setText(movie.getTitle());
                    tvDesc.setText(movie.getDescription());
                    tvLength.setText(movie.getLength() + " min");
                    tvStaffId.setText(String.valueOf(movie.getStaffID()));

                    String poster = movie.getImagePoster();

                    // Use ApiUtils.UPLOADS_URL to construct full poster URL
                    if (poster != null && !poster.startsWith("http")) {
                        poster = ApiUtils.UPLOADS_URL + poster;
                    }

                    Log.d("IMG_URL", poster);

                    Glide.with(MovieDetailsActivity.this)
                            .load(poster)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imgPoster);

                } else {
                    String errorMsg = "Failed: " + response.code();
                    Log.e("MovieDetails", errorMsg);
                    Toast.makeText(MovieDetailsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e("MovieDetails", errorMsg);
                Toast.makeText(MovieDetailsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
