package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class LoginActivity extends AppCompatActivity {

    private EditText edtLogin, edtPassword;
    private Button btnLogin;
    private RadioGroup rgUserType;
    private RadioButton rbCustomer, rbStaff;
    private TextView tvRegister;

    private CustService custService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Auto-redirect if already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
            return;
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Bind views
        edtLogin = findViewById(R.id.edtUsername);       // Name or Email
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        rgUserType = findViewById(R.id.rgUserType);   // RadioGroup for Customer/Staff
        rbCustomer = findViewById(R.id.rbCustomer);
        rbStaff = findViewById(R.id.rbStaff);
        tvRegister = findViewById(R.id.textViewRegister);   // TextView "Register Here"

        custService = ApiUtils.getCustService();

        // Login button click
        btnLogin.setOnClickListener(v -> doLogin());

        // Register TextView click
        tvRegister.setOnClickListener(v -> {
            if (rbCustomer.isChecked()) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            } else if (rbStaff.isChecked()) {
                startActivity(new Intent(LoginActivity.this, SignUpStaffActivity.class));
            } else {
                Toast.makeText(this, "Please select Customer or Staff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle toolbar back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate to MenuActivity instead of finishing
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void doLogin() {
        String login = edtLogin.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        Call<Customer> call;

        if (Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            call = custService.loginEmail(login, password);
        } else {
            call = custService.login(login, password);
        }

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Customer user = response.body();
                    SharedPrefManager.getInstance(LoginActivity.this).storeUser(user);

                    Toast.makeText(LoginActivity.this,
                            "Welcome " + user.getUsername() + " 👋",
                            Toast.LENGTH_SHORT).show();

                    Intent intent;
                    if (rbStaff.isChecked()) {
                        intent = new Intent(LoginActivity.this, StaffHomeActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MenuActivity.class);
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login failed. Invalid credentials.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
