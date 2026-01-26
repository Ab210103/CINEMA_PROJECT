package com.example.cinema_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMovieActivity extends AppCompatActivity {

    private static final int PICK_BANNER = 100;
    private static final int PICK_POSTER = 101;

    private EditText etTitle, etDesc, etGenre, etLength;
    private ImageView imgBanner, imgPoster;
    private Button btnUploadBanner, btnUploadPoster, btnUpdateMovie;

    private String bannerString, posterString;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarUpdateMovie);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        etTitle = findViewById(R.id.etMovieTitle);
        etDesc = findViewById(R.id.etMovieDesc);
        etGenre = findViewById(R.id.etMovieGenre);
        etLength = findViewById(R.id.etMovieLength);
        imgBanner = findViewById(R.id.imgBanner);
        imgPoster = findViewById(R.id.imgPoster);
        btnUploadBanner = findViewById(R.id.btnUploadBanner);
        btnUploadPoster = findViewById(R.id.btnUploadPoster);
        btnUpdateMovie = findViewById(R.id.btnUpdateMovie);

        // Image pickers
        btnUploadBanner.setOnClickListener(v -> pickImage(PICK_BANNER));
        btnUploadPoster.setOnClickListener(v -> pickImage(PICK_POSTER));

        // Load movie ID from intent
        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId != -1) {
            loadMovie(movieId);
        }

        // Update movie button
        btnUpdateMovie.setOnClickListener(v -> updateMovie());
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                // Convert bitmap to Base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] bytes = baos.toByteArray();
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

                if (requestCode == PICK_BANNER) {
                    bannerString = encoded;
                    imgBanner.setImageBitmap(bitmap);
                } else if (requestCode == PICK_POSTER) {
                    posterString = encoded;
                    imgPoster.setImageBitmap(bitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadMovie(int movieId) {
        SharedPrefManager spm = new SharedPrefManager(this);
        String apiKey = spm.getToken();

        MovieService movieService = ApiUtils.getMovieService();
        movieService.getMovie(apiKey, movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movie = response.body();

                    etTitle.setText(movie.getTitle());
                    etDesc.setText(movie.getDescription());
                    etGenre.setText(movie.getGenre());
                    etLength.setText(movie.getLength());

                    bannerString = movie.getImageBanner();
                    posterString = movie.getImagePoster();

                    // Decode and display images
                    if (bannerString != null) {
                        byte[] bannerBytes = Base64.decode(bannerString, Base64.DEFAULT);
                        imgBanner.setImageBitmap(BitmapFactory.decodeByteArray(bannerBytes, 0, bannerBytes.length));
                    }
                    if (posterString != null) {
                        byte[] posterBytes = Base64.decode(posterString, Base64.DEFAULT);
                        imgPoster.setImageBitmap(BitmapFactory.decodeByteArray(posterBytes, 0, posterBytes.length));
                    }

                } else if (response.code() == 401) {
                    Toast.makeText(UpdateMovieActivity.this, "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    spm.logout();
                    startActivity(new Intent(UpdateMovieActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(UpdateMovieActivity.this, "Error loading movie: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(UpdateMovieActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMovie() {
        if (movie == null) return;

        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String lengthStr = etLength.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || genre.isEmpty() || lengthStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int length;
        try {
            length = Integer.parseInt(lengthStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Length must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPrefManager spm = new SharedPrefManager(this);
        String apiKey = spm.getToken();

        MovieService movieService = ApiUtils.getMovieService();
        Call<Movie> call = movieService.updateMovie(
                apiKey,
                movie.getId(),
                title,
                desc,
                length,
                genre,
                bannerString,
                posterString
        );

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showAlert("Movie updated successfully!");
                } else if (response.code() == 401) {
                    Toast.makeText(UpdateMovieActivity.this, "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    spm.logout();
                    startActivity(new Intent(UpdateMovieActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(UpdateMovieActivity.this, "Error updating movie: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(UpdateMovieActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }
}
