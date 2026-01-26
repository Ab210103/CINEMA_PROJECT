package com.example.cinema_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    // form fields
    private EditText etNames, etPhones, etEmails, etPasswords;

    private SharedPrefManager spm;
    private Customer customer; // current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbarAddMovie);
        toolbar.setNavigationOnClickListener(v -> finish());

        etNames = findViewById(R.id.etnames);
        etPhones = findViewById(R.id.etphonesnums);
        etEmails = findViewById(R.id.etemails);
        etPasswords = findViewById(R.id.etpasswords);

        spm = new SharedPrefManager(getApplicationContext());
        customer = spm.getUser();

        // populate form
        if (customer != null) {
            etNames.setText(customer.getUsername());
            etPhones.setText(customer.getPhoneNumber());
            etEmails.setText(customer.getEmail());
            etPasswords.setText(customer.getPassword());
        }
    }

    /**
     * Called when Update Profile button clicked
     */
    public void updateProfile(android.view.View view) {

        String name = etNames.getText().toString().trim();
        String phone = etPhones.getText().toString().trim();
        String email = etEmails.getText().toString().trim();
        String password = etPasswords.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("MyApp:", "Old Profile: " + customer.toString());

        // update object
        customer.setUsername(name);
        customer.setPhoneNumber(phone);
        customer.setEmail(email);
        customer.setPassword(password);

        Log.d("MyApp:", "New Profile: " + customer.toString());

        CustService custService = ApiUtils.getCustService();
        Call<Customer> call = custService.updateUser(
                spm.getToken(),
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getPassword(),
                customer.getGender(),
                customer.getProfession()
        );

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {

                Log.d("MyApp:", "Update Profile Response: " + response.raw());

                if (response.code() == 200 && response.body() != null) {

                    Customer updatedUser = response.body();
                    spm.storeUser(updatedUser);

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
                displayAlert("Network Error: " + t.getMessage());
                Log.e("MyApp:", t.toString());
            }
        });
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
