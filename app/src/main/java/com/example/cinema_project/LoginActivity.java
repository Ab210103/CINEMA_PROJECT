package com.example.cinema_project;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.model.FailLogin;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;
import com.example.cinema_project.sharedpref.SharedPrefManager;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private ProgressBar progressBar;
    private TextView textViewRegister;
    private RadioGroup rgUserType;
    private RadioButton rbCustomer, rbStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);

        // Enable back button
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // get references to form elements
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
        rgUserType = findViewById(R.id.rgUserType);
        rbCustomer = findViewById(R.id.rbCustomer);
        rbStaff = findViewById(R.id.rbStaff);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(GONE);

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (rbCustomer.isChecked()) {
                    // Pergi SignupCustomerActivity
                    intent = new Intent(LoginActivity.this, SignUpActivity.class);
                } else {
                    // Pergi SignupStaffActivity
                    intent = new Intent(LoginActivity.this, SignUpStaffActivity.class);
                }
                startActivity(intent);
            }
        });
    }

    // Handle back button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            // Back to HomeFragment
            onBackPressed(); //close LoginActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Login button action handler
     */
    public void loginClicked(View view) {

        // get username and password entered by user
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        // validate form, make sure it is not empty
        if (validateLogin(username, password)) {
            // if not empty, login using REST API
            doLogin(username, password);
        }

    }

    /**
     * Call REST API to login
     *
     * @param username username
     * @param password password
     */
    private void doLogin(String username, String password) {

        // get UserService instance
        CustService custService = ApiUtils.getCustService();

        // prepare the REST API call using the service interface
        Call<Customer> call;
        if (username.contains("@")) {
            call = custService.loginEmail(username, password);
        } else {
            call = custService.login(username, password);
        }

        // display progress Bar
        progressBar.setVisibility(VISIBLE);

        // execute the REST API call
        call.enqueue(new Callback<Customer>() {

            @Override
            public void onResponse(Call call, Response response) {

                // set progress bar to gone
                progressBar.setVisibility(GONE);

                if (response.isSuccessful()) {  // code 200
                    // parse response to POJO
                    Customer user = (Customer) response.body();
                    if (user != null ) {
                        // successful login. server replies a token value
                        displayToast("Login successful");

                        // store value in Shared Preferences
                        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
                        spm.storeUser(user);

                        // forward user to MenuActivity
                        finish();
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(intent);

                    } else {
                        // server return success but no user info replied
                        displayToast("Login error");
                    }
                } else {  // other than 200
                    // try to parse the response to FailLogin POJO
                    String errorResp = null;
                    try {
                        errorResp = response.errorBody().string();
                        FailLogin e = new Gson().fromJson(errorResp, FailLogin.class);
                        displayToast(e.getError().getMessage());
                    } catch (Exception e) {
                        Log.e("MyApp:", e.toString()); // print error details to error log
                        displayToast("Error");
                    }
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {

                // set progress bar to gone
                progressBar.setVisibility(GONE);

                displayToast("Error connecting to server.");
                displayToast(t.getMessage());
                Log.e("MyApp:", t.toString()); // print error details to error log
            }
        });
    }

    /**
     * Validate value of username and password entered. Client side validation.
     * @param username
     * @param password
     * @return
     */
    private boolean validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            displayToast("Username is required");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            displayToast("Password is required");
            return false;
        }
        return true;
    }

    /**
     * Display a Toast message
     * @param message message to be displayed inside toast
     */
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}