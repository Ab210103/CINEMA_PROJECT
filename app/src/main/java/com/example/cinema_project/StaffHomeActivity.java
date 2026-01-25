package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StaffHomeActivity extends AppCompatActivity {

    private Button btnMovieList, btnProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);

        Toolbar toolbar = findViewById(R.id.toolbarStaff);
        setSupportActionBar(toolbar);

        btnMovieList = findViewById(R.id.btnMovieList);
        btnProfile   = findViewById(R.id.btnProfile);
        btnLogout    = findViewById(R.id.btnLogout);

        btnMovieList.setOnClickListener(v -> {
            //startActivity(new Intent(this, StaffMovieListActivity.class));
        });

        btnProfile.setOnClickListener(v -> {
            //startActivity(new Intent(this, StaffProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
