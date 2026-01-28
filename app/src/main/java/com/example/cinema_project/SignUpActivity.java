package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtPhone;
    private RadioGroup rgGender;
    private Spinner spinnerProfession;
    private Button btnSignUp;

    private CustService custService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSignUp);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Bind views
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtfon);
        edtPassword = findViewById(R.id.edtPassword);
        rgGender = findViewById(R.id.rgGender);
        spinnerProfession = findViewById(R.id.spinnerProfession);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Spinner setup
        String[] professions = {"Student", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_white,
                professions
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinnerProfession.setAdapter(adapter);

        // Retrofit service
        custService = ApiUtils.getCustService();

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String profession = spinnerProfession.getSelectedItem().toString();

        // Gender mapping example
        String gender = "";
        int selectedId = rgGender.getCheckedRadioButtonId();
        if (selectedId == R.id.rbMale) {
            gender = "Male";
        } else if (selectedId == R.id.rbFemale) {
            gender = "Female";
        }

        if (profession.equalsIgnoreCase("Student")) {
            profession = "Student";
        } else {
            profession = "Other";
        }

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

        btnSignUp.setEnabled(false);

        String token = UUID.randomUUID().toString();

        // Debug log
        Log.d("REGISTER_DATA", email + ", " + username + ", " + gender + ", " + profession + ", " + phone + ", " + token);

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
                    Customer user = response.body();

                    // Save user to SharedPref
                    SharedPrefManager.getInstance(SignUpActivity.this).storeUser(user);

                    Toast.makeText(SignUpActivity.this,
                            "Welcome " + user.getUsername() + "! Registration successful 🎉",
                            Toast.LENGTH_SHORT).show();

                    // Navigate based on role
                    Intent intent;
                    if ("staff".equalsIgnoreCase(user.getRole())) {
                        intent = new Intent(SignUpActivity.this, StaffHomeActivity.class);
                    } else {
                        intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(SignUpActivity.this,
                            "Registration failed. Code: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                btnSignUp.setEnabled(true);
                Toast.makeText(SignUpActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
