package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

        // Gender mapping
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

        // Hash the password using MD5
        String hashedPassword = md5(password);

        // Debug log
        Log.d("REGISTER_DATA", email + ", " + username + ", " + gender + ", " + profession + ", " + phone + ", hashedPwd: " + hashedPassword);

        String tetoken = "4716791f-c555-4b63-a8e1-86a38925473e";
        // API call with MD5 password
        Call<Customer> call = custService.signUp(
                tetoken,
                email,
                username,
                hashedPassword, // MD5 password
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
                    Toast.makeText(SignUpActivity.this,
                            "Customer registered successfully 👔",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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

    // MD5 hashing helper method
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);

            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
