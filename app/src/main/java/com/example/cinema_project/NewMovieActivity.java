package com.example.cinema_project;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
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

public class NewMovieActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etGenre, etLength;
    private Button uploadBanner, uploadPoster, btnAddMovie;
    private ImageView imgBanner, imgPoster;

    private static final int PICK_BANNER = 1;
    private static final int PICK_POSTER = 2;

    private Uri bannerUri = null;
    private Uri posterUri = null;
    private String bannerFileName, posterFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movie);

        // Toolbar back button
        Toolbar toolbar = findViewById(R.id.toolbarAddMovie);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(NewMovieActivity.this, StaffHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Views
        etTitle = findViewById(R.id.etMovieTitle);
        etDesc = findViewById(R.id.etMovieDesc);
        etGenre = findViewById(R.id.etMovieGenre);
        etLength = findViewById(R.id.etMovieLength);
        imgBanner = findViewById(R.id.imgBanner);
        imgPoster = findViewById(R.id.imgPoster);

        uploadBanner = findViewById(R.id.btnUploadBanner);
        uploadPoster = findViewById(R.id.btnUploadPoster);
        btnAddMovie = findViewById(R.id.btnAddMovie);

        // Button listeners
        uploadBanner.setOnClickListener(v -> pickImage(PICK_BANNER));
        uploadPoster.setOnClickListener(v -> pickImage(PICK_POSTER));

        btnAddMovie.setOnClickListener(v -> {
            if (bannerUri != null && posterUri != null) {
                uploadFile(bannerUri, true); // upload banner first
            } else {
                Toast.makeText(this, "Please select both banner and poster", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    // Upload file to server
    private void uploadFile(Uri fileUri, boolean isBanner) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] bytes = getBytesFromInputStream(inputStream);

            RequestBody reqFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), bytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", getFileName(fileUri), reqFile);

            String token = SharedPrefManager.getInstance(this).getUser().getToken();
            MovieService movieService = ApiUtils.getMovieService();
            Call<FileInfo> call = movieService.uploadFile(token, body);

            call.enqueue(new Callback<FileInfo>() {
                @Override
                public void onResponse(Call<FileInfo> call, Response<FileInfo> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (isBanner) {
                            bannerFileName = response.body().getFile();
                            uploadFile(posterUri, false); // upload poster next
                        } else {
                            posterFileName = response.body().getFile();
                            addMovieRecord(); // all files uploaded, add movie
                        }
                    } else {
                        Toast.makeText(NewMovieActivity.this, "File upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FileInfo> call, Throwable t) {
                    Toast.makeText(NewMovieActivity.this, "Error uploading file: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing file", Toast.LENGTH_SHORT).show();
        }
    }

    // Add movie record
    private void addMovieRecord() {
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

        Customer user = SharedPrefManager.getInstance(this).getUser();
        MovieService movieService = ApiUtils.getMovieService();

        Call<Movie> call = movieService.addMovie(
                user.getToken(),
                title,
                desc,
                length,
                genre,
                user.getId(),
                bannerFileName,
                posterFileName
        );

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NewMovieActivity.this, title + " added successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else if (response.code() == 401) {
                    Toast.makeText(NewMovieActivity.this, "Invalid session. Please login again", Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(NewMovieActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                    Log.e("NewMovieActivity", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(NewMovieActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("NewMovieActivity", "Failure: " + t.getCause());
            }
        });
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager.getInstance(this).logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
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
