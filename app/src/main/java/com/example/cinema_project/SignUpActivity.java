package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
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
import com.example.cinema_project.model.RegisterResponse;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtphonenum;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Spinner spinnerProfession;
    private Button btnSignUp;

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
        edtphonenum = findViewById(R.id.edtfon);
        edtPassword = findViewById(R.id.edtPassword);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
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

        // Sign Up button click
        btnSignUp.setOnClickListener(v -> {

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String fon = edtphonenum.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String profession = spinnerProfession.getSelectedItem().toString();

            // 🔥 GET GENDER PROPERLY
            String gender = "";
            int selectedGenderId = rgGender.getCheckedRadioButtonId();

            if (selectedGenderId == R.id.rbMale) {
                gender = "Male";
            } else if (selectedGenderId == R.id.rbFemale) {
                gender = "Female";
            }

            // 🔥 VALIDATION
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (gender.isEmpty()) {
                Toast.makeText(this, "Please select gender!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 5) {
                Toast.makeText(this, "Password must be at least 5 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 Retrofit API call (NO Customer object needed)
            CustService service = ApiUtils.getCustService();

            Call<RegisterResponse> call = service.signUp(
                    name,
                    email,
                    fon,
                    password,
                    gender,
                    profession
            );

            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {

                        if ("success".equalsIgnoreCase(response.body().getStatus())) {
                            Toast.makeText(SignUpActivity.this,
                                    "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SignUpActivity.this,
                                "Server Error " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this,
                            "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Toolbar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
