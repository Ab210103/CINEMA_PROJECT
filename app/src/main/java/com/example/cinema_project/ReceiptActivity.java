package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cinema_project.sharedpref.SharedPrefManager;

public class ReceiptActivity extends AppCompatActivity {

    private TextView txtCustomer, txtMovie, txtDate, txtTime,
            txtQty, txtSeat, txtPayment, txtPrice;
    private Button btnDone, btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Bind views
        txtCustomer = findViewById(R.id.txtCustomer);
        txtMovie = findViewById(R.id.txtMovie);
        txtDate = findViewById(R.id.txtDate);
        txtTime = findViewById(R.id.txtTime);
        txtQty = findViewById(R.id.txtQty);
        txtSeat = findViewById(R.id.txtSeat);
        txtPayment = findViewById(R.id.txtPayment);
        txtPrice = findViewById(R.id.txtPrice);

        btnDone = findViewById(R.id.btnDone);
        btnPrint = findViewById(R.id.btnPrint);

        // Get intent data from DetailsActivity
        String movieTitle = getIntent().getStringExtra("title");
        String seats = getIntent().getStringExtra("seats");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        int ticketQty = getIntent().getIntExtra("ticketQty", 1);
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
        String paymentType = getIntent().getStringExtra("paymentType");

        // Fetch logged-in customer name
        SharedPrefManager spm = new SharedPrefManager(this);
        String customerName = "Guest"; // default
        if (spm.isLoggedIn()) {
            customerName = spm.getUser().getUsername();
        }
        txtCustomer.setText(customerName);


        // Set values to views
        txtCustomer.setText(customerName);
        txtMovie.setText(movieTitle);
        txtDate.setText(date);
        txtTime.setText(time);
        txtQty.setText(String.valueOf(ticketQty));
        txtSeat.setText(seats);
        txtPayment.setText(paymentType);
        txtPrice.setText("RM " + String.format("%.2f", totalPrice));

        // Button actions
        btnDone.setOnClickListener(v -> {
            // Go back to MenuActivity
            Intent intent = new Intent(ReceiptActivity.this, MenuActivity.class);
            // Clear back stack so user can't go back to DetailsActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnPrint.setOnClickListener(v ->
                Toast.makeText(ReceiptActivity.this,
                        "Printing receipt...", Toast.LENGTH_SHORT).show());
    }
}
