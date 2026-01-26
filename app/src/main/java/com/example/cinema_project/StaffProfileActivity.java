package com.example.cinema_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvGender, tvProfession;
    private Button btnEdit;
    private CustService custService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbarMovieList);
        toolbar.setTitle("STAFF PROFILE");
        toolbar.setNavigationIcon(R.drawable.arrow_back); // your back arrow
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvProfession = findViewById(R.id.tvProfession);
        btnEdit = findViewById(R.id.btnEdit);

        custService = ApiUtils.getCustService();

        // Fetch token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token != null) {
            fetchStaffProfile(token);
        } else {
            // fallback if not logged in
            tvName.setText("Guest");
            tvEmail.setText("-");
            tvPhone.setText("-");
            tvGender.setText("-");
            tvProfession.setText("-");
        }

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(StaffProfileActivity.this, StaffEditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchStaffProfile(String token) {
        // Call API to get staff profile
        Call<Customer> call = custService.getProfile("Bearer " + token);
        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(@NonNull Call<Customer> call, @NonNull Response<Customer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Customer staff = response.body();

                    tvName.setText(staff.getUsername() != null ? staff.getUsername() : "-");
                    tvEmail.setText(staff.getEmail() != null ? staff.getEmail() : "-");
                    tvPhone.setText(staff.getPhoneNumber() != null ? staff.getPhoneNumber() : "-");
                    tvGender.setText(staff.getGender() != null ? staff.getGender() : "-");
                    tvProfession.setText(staff.getProfession() != null ? staff.getProfession() : "-");

                } else {
                    Toast.makeText(StaffProfileActivity.this,
                            "Failed to load profile: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                Toast.makeText(StaffProfileActivity.this,
                        "API Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Toolbar back button pressed
    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(StaffProfileActivity.this, StaffHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
