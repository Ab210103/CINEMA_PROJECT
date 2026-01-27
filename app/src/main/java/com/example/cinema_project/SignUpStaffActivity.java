package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpStaffActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtPhone;
    private RadioGroup rgGender;
    private RadioButton rbMale , rbFemale;
    private Button btnSignUp;

    private CustService custService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_staff);

        Toolbar toolbar = findViewById(R.id.toolbarSignUpStaff);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtphones);
        edtPassword = findViewById(R.id.edtPassword);
        rgGender = findViewById(R.id.rgGender);
        btnSignUp = findViewById(R.id.btnSignUp);

        custService = ApiUtils.getCustService();

        btnSignUp.setOnClickListener(v -> registerStaff());
    }

    private void registerStaff() {
        String username = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Field validation
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 5) {
            Toast.makeText(this, "Password must be at least 5 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gender mapping example
        String gender = "";
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedId == R.id.rbFemale) {
            gender = "Female";
        }

        String profession = "staff";

        btnSignUp.setEnabled(false);
        String token = UUID.randomUUID().toString();

        String tetoken = "1cd4b43d-e4e1-4920-9805-cc3f6826d969";
        // API call
        Call<Customer> call = custService.signUp(
                tetoken,
                email,
                username,
                password,
                gender,
                profession,
                phone,
                token
        );

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                btnSignUp.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Customer staff = response.body();

                    Toast.makeText(SignUpStaffActivity.this,
                            "Welcome " + staff.getUsername() + "! Staff registered successfully 👔",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpStaffActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(SignUpStaffActivity.this,
                            "Registration failed. Code: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                btnSignUp.setEnabled(true);
                Toast.makeText(SignUpStaffActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
