package com.example.cinema_project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class EditProfileActivity extends AppCompatActivity {

    private EditText etNames, etPhones, etEmails, etPasswords;
    private Button updateprofile;

    private SharedPrefManager spm;
    private Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbarAddMovie);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etNames = findViewById(R.id.etnames);
        etPhones = findViewById(R.id.etphonesnums);
        etEmails = findViewById(R.id.etemails);
        etPasswords = findViewById(R.id.etpasswords);
        updateprofile = findViewById(R.id.btnEdit);

        spm = SharedPrefManager.getInstance(this);

        // 🔐 Check login
        if (!spm.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        customer = spm.getUser();

        if (customer != null) {
            etNames.setText(customer.getUsername());
            etPhones.setText(customer.getPhoneNumber());
            etEmails.setText(customer.getEmail());
            // ❗ Jangan auto isi password lama (security)
            etPasswords.setText(customer.getPassword());
        }

        updateprofile.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String name = etNames.getText().toString().trim();
        String phone = etPhones.getText().toString().trim();
        String email = etEmails.getText().toString().trim();
        String password = etPasswords.getText().toString().trim();
        String hashedPassword = md5(password);

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }

        // Update object
        customer.setUsername(name);
        customer.setPhoneNumber(phone);
        customer.setEmail(email);

        if (!password.isEmpty()) {
            customer.setPassword(hashedPassword); // only update if user typed new password
        }

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
                Log.d("EditProfile", "Response: " + response.raw());

                if (response.isSuccessful() && response.body() != null) {

                    Customer updatedUser = response.body();
                    spm.storeUser(updatedUser);

                    showSuccessDialog("Profile updated successfully");

                } else if (response.code() == 401) {
                    Toast.makeText(EditProfileActivity.this,
                            "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    spm.logout();
                    startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                    finish();

                } else {
                    Toast.makeText(EditProfileActivity.this,
                            "Update failed: " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                showErrorDialog("Network error: " + t.getMessage());
                Log.e("EditProfile", t.toString());
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
    private void showSuccessDialog(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", (d, i) -> {
                    d.dismiss();
                    finish();
                })
                .show();
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("OK", (d, i) -> d.dismiss())
                .show();
    }
}
