package com.example.cinema_project;

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

public class NewMovieActivity extends AppCompatActivity {

    private static final int PICK_BANNER = 100;
    private static final int PICK_POSTER = 101;

    private EditText etTitle, etDesc, etGenre, etLength;
    private ImageView imgBanner, imgPoster;
    private Button btnUploadBanner, btnUploadPoster, btnAddMovie;

    private String bannerString, posterString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movie);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAddMovie);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Form fields
        etTitle = findViewById(R.id.etMovieTitle);
        etDesc = findViewById(R.id.etMovieDesc);
        etGenre = findViewById(R.id.etMovieGenre);
        etLength = findViewById(R.id.etMovieLength);

        // ImageViews
        imgBanner = findViewById(R.id.imgBanner);
        imgPoster = findViewById(R.id.imgPoster);

        // Buttons
        btnUploadBanner = findViewById(R.id.btnUploadBanner);
        btnUploadPoster = findViewById(R.id.btnUploadPoster);
        btnAddMovie = findViewById(R.id.btnAddMovie);

        // Image pickers
        btnUploadBanner.setOnClickListener(v -> pickImage(PICK_BANNER));
        btnUploadPoster.setOnClickListener(v -> pickImage(PICK_POSTER));

        // Add movie
        btnAddMovie.setOnClickListener(v -> addMovie());
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

                // Convert bitmap to Base64 string with MIME type prefix
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] bytes = baos.toByteArray();
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                String base64WithPrefix = "data:image/jpeg;base64," + encoded;

                if (requestCode == PICK_BANNER) {
                    bannerString = base64WithPrefix;
                    imgBanner.setImageBitmap(bitmap);
                } else if (requestCode == PICK_POSTER) {
                    posterString = base64WithPrefix;
                    imgPoster.setImageBitmap(bitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMovie() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String lengthStr = etLength.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || genre.isEmpty() || lengthStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bannerString == null || posterString == null) {
            Toast.makeText(this, "Please upload both banner and poster images", Toast.LENGTH_SHORT).show();
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
        int staffId = spm.getUser().getId();
        String apiKey = spm.getToken();

        // Call API
        MovieService movieService = ApiUtils.getMovieService();
        Call<Movie> call = movieService.addMovie(
                apiKey,
                title,
                desc,
                length,
                genre,
                staffId,
                bannerString,  // already has data:image/jpeg;base64, prefix
                posterString  // already has data:image/jpeg;base64, prefix
        );

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(NewMovieActivity.this, "Movie added successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewMovieActivity.this, StaffHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(NewMovieActivity.this, "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    spm.logout();
                    startActivity(new Intent(NewMovieActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(NewMovieActivity.this, "Failed to add movie: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(NewMovieActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
