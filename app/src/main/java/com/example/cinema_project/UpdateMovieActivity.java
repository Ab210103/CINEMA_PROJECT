package com.example.cinema_project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cinema_project.model.FileInfo;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateMovieActivity extends AppCompatActivity {

    private static final int PICK_BANNER = 100;
    private static final int PICK_POSTER = 101;

    private EditText etTitle, etDesc, etGenre, etLength;
    private ImageView imgBanner, imgPoster;
    private Button btnUploadBanner, btnUploadPoster, btnUpdateMovie;

    private Uri bannerUri = null;
    private Uri posterUri = null;

    private String bannerFileName, posterFileName;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_movie);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarUpdateMovie);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Update Movie");
        }

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

        // Pick images
        btnUploadBanner.setOnClickListener(v -> pickImage(PICK_BANNER));
        btnUploadPoster.setOnClickListener(v -> pickImage(PICK_POSTER));

        // Load movie ID from intent
        int movieId = getIntent().getIntExtra("movieCode", -1);
        if (movieId != -1) loadMovie(movieId);

        // Update button
        btnUpdateMovie.setOnClickListener(v -> {
            if (bannerUri != null) {
                uploadFile(bannerUri, true);
            } else if (posterUri != null) {
                uploadFile(posterUri, false);
            } else {
                updateMovieRecord();
            }
        });
    }

    // Toolbar back button
    @Override
    public boolean onSupportNavigateUp() {
        finish(); // simply finish this activity to go back
        return true;
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (requestCode == PICK_BANNER) {
                bannerUri = uri;
                imgBanner.setImageURI(uri);
            } else if (requestCode == PICK_POSTER) {
                posterUri = uri;
                imgPoster.setImageURI(uri);
            }
        }
    }

    // Load movie and prefill form
    private void loadMovie(int movieId) {
        String token = SharedPrefManager.getInstance(this).getUser().getToken();
        ApiUtils.getMovieService().getMovie(token, movieId).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    movie = response.body();

                    // Prefill form fields
                    etTitle.setText(movie.getTitle());
                    etDesc.setText(movie.getDescription());
                    etGenre.setText(movie.getGenre());
                    etLength.setText(String.valueOf(movie.getLength()));

                    bannerFileName = movie.getImageBanner();
                    posterFileName = movie.getImagePoster();

                    // Load images with Glide
                    if (bannerFileName != null && !bannerFileName.isEmpty()) {
                        Glide.with(UpdateMovieActivity.this)
                                .load(ApiUtils.UPLOADS_URL + bannerFileName)
                                .into(imgBanner);
                    }
                    if (posterFileName != null && !posterFileName.isEmpty()) {
                        Glide.with(UpdateMovieActivity.this)
                                .load(ApiUtils.UPLOADS_URL + posterFileName)
                                .into(imgPoster);
                    }
                } else if (response.code() == 401) {
                    handleSessionExpired();
                } else {
                    Toast.makeText(UpdateMovieActivity.this, "Error loading movie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(UpdateMovieActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Handle session expired
    private void handleSessionExpired() {
        Toast.makeText(UpdateMovieActivity.this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
        SharedPrefManager.getInstance(this).logout();
        startActivity(new Intent(UpdateMovieActivity.this, LoginActivity.class));
        finish();
    }


    private void uploadFile(Uri fileUri, boolean isBanner) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] bytes = getBytesFromInputStream(inputStream);
            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "file", getFileName(fileUri),
                    RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), bytes)
            );

            String token = SharedPrefManager.getInstance(this).getUser().getToken();
            ApiUtils.getMovieService().uploadFile(token, body).enqueue(new Callback<FileInfo>() {
                @Override
                public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (isBanner) {
                            bannerFileName = response.body().getFile();
                            if (posterUri != null) uploadFile(posterUri, false);
                            else updateMovieRecord();
                        } else {
                            posterFileName = response.body().getFile();
                            updateMovieRecord();
                        }
                    } else {
                        Toast.makeText(UpdateMovieActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileInfo> call, Throwable t) {
                    Toast.makeText(UpdateMovieActivity.this, "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing file", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMovieRecord() {
        if (movie == null) return;

        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        int length;
        try {
            length = Integer.parseInt(etLength.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Length must be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = SharedPrefManager.getInstance(this).getUser().getToken();
        ApiUtils.getMovieService().updateMovie(
                token, movie.getId(), title, desc, length, genre, bannerFileName, posterFileName
        ).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateMovieActivity.this, "Movie updated successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(UpdateMovieActivity.this, "Session expired", Toast.LENGTH_SHORT).show();
                    SharedPrefManager.getInstance(UpdateMovieActivity.this).logout();
                    startActivity(new Intent(UpdateMovieActivity.this, LoginActivity.class));
                    finish();
                } else {
                    Toast.makeText(UpdateMovieActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(UpdateMovieActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
