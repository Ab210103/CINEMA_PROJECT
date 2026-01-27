package com.example.cinema_project;

import android.content.Intent;
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
import com.example.cinema_project.sharedpref.SharedPrefManager;

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

        Toolbar toolbar = findViewById(R.id.toolbarstaffProfile);
        toolbar.setTitle("STAFF PROFILE");
        toolbar.setNavigationIcon(R.drawable.arrow_back);
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

        refreshProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshProfile();
    }

    private void refreshProfile() {
        SharedPrefManager spm = SharedPrefManager.getInstance(this);

        if (spm.isLoggedIn()) {
            // User logged in
            Customer user = spm.getUser();

            tvName.setText(user.getUsername() != null ? user.getUsername() : "-");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "-");
            tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
            tvGender.setText(user.getGender() != null ? user.getGender() : "-");
            tvProfession.setText(user.getProfession() != null ? user.getProfession() : "-");

            btnEdit.setVisibility(Button.VISIBLE);

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(StaffProfileActivity.this, StaffEditProfileActivity.class);
                startActivity(intent);
            });


        } else {
            // Guest mode
            tvName.setText("Guest");
            tvEmail.setText("-");
            tvPhone.setText("-");
            tvGender.setText("-");
            tvProfession.setText("-");

            btnEdit.setVisibility(Button.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
