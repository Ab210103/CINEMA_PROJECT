package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.*;
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

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, MenuActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        edtLogin = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        rgUserType = findViewById(R.id.rgUserType);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbStaff = findViewById(R.id.rbStaff);
        tvRegister = findViewById(R.id.textViewRegister);

        custService = ApiUtils.getCustService();

        btnLogin.setOnClickListener(v -> doLogin());

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

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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
            Log.d("LOGIN", "Logging in with email: " + login);
        } else {
            call = custService.login(login, password);
            Log.d("LOGIN", "Logging in with username: " + login);
        }

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                btnLogin.setEnabled(true);

                Log.d("LOGIN", "Response code: " + response.code());

                if (response.body() != null) {
                    Log.d("LOGIN", "Response body: " + response.body().toString());
                } else if (response.errorBody() != null) {
                    try {
                        Log.d("LOGIN", "Error body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

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
                Log.e("LOGIN", "Network error", t);
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
