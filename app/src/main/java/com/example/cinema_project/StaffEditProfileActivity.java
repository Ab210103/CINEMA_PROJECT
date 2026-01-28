package com.example.cinema_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffEditProfileActivity extends AppCompatActivity {

    // form fields
    private EditText etNames, etPhones, etEmails, etPasswords;
    private Button updateprofiles;
    private Customer customer; // current logged-in staff
    private SharedPrefManager spm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_edit_profile);

        // toolbar back button
        Toolbar toolbar = findViewById(R.id.toolbarAddMovie);
        toolbar.setNavigationOnClickListener(v -> finish());

        // init views
        etNames = findViewById(R.id.etnames);
        etPhones = findViewById(R.id.etphonesnums);
        etEmails = findViewById(R.id.etemails);
        etPasswords = findViewById(R.id.etpasswords);
        updateprofiles = findViewById(R.id.btnstaffedit);

        // SharedPref
        spm = new SharedPrefManager(getApplicationContext());

        // get current user
        customer = spm.getUser();

        // populate form
        if (customer != null) {
            etNames.setText(customer.getUsername());
            etPhones.setText(customer.getPhoneNumber());
            etEmails.setText(customer.getEmail());
            etPasswords.setText(customer.getPassword());
        }

        updateprofiles.setOnClickListener(v -> updateProfiles(v));
    }

    /**
     * Called when Update Profile button clicked
     */
    public void updateProfiles(View view) {

        String name = etNames.getText().toString().trim();
        String phone = etPhones.getText().toString().trim();
        String email = etEmails.getText().toString().trim();
        String password = etPasswords.getText().toString().trim();
        String hashedPassword = md5(password);

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("MyApp:", "Old Profile: " + customer.toString());

        // update object
        customer.setUsername(name);
        customer.setPhoneNumber(phone);
        customer.setEmail(email);

        if (!password.isEmpty()) {
            customer.setPassword(hashedPassword); // only update if user typed new password
        }
        Log.d("MyApp:", "New Profile: " + customer.toString());

        // API
        CustService custService = ApiUtils.getCustService();
        Call<Customer> call = custService.updateUser(
                spm.getToken(),
                customer.getId(),
                customer.getEmail(),
                customer.getUsername(),
                customer.getPassword(),
                customer.getGender(),
                customer.getProfession(),
                customer.getPhoneNumber()
        );

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {

                Log.d("MyApp:", "Update Profile Response: " + response.raw());

                if (response.code() == 200 && response.body() != null) {

                    Customer updatedCustomer = response.body();

                    // update shared pref
                    spm.storeUser(updatedCustomer);

                    displayUpdateSuccess("Profile updated successfully.");

                } else if (response.code() == 401) {

                    Toast.makeText(getApplicationContext(),
                            "Invalid session. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                displayAlert("Error: " + t.getMessage());
                Log.e("MyApp:", t.toString());
            }
        });
    }

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

    /**
     * Clear session & redirect to login
     */
    private void clearSessionAndRedirect() {
        spm.logout();
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * Success dialog
     */
    private void displayUpdateSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * Error dialog
     */
    private void displayAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        builder.create().show();
    }
}
