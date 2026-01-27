package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinema_project.sharedpref.SharedPrefManager;

public class ReceiptActivity extends AppCompatActivity {

    private TextView txtCustomer, txtMovie, txtDate, txtTime,
            txtQty, txtSeat, txtPayment, txtPrice;
    private Button btnDone, btnPrint;

    // Booking data passed from DetailsActivity
    private String customerName, movieTitle, date, time, seat, paymentType;
    private int quantity, movieCode;
    private double totalPrice;

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

        // Get username for customer
        SharedPrefManager spm = SharedPrefManager.getInstance(this);
        customerName = spm.isLoggedIn() ? spm.getUser().getUsername() : "Guest";

        // Get data from Intent
        Intent intent = getIntent();
        movieTitle = intent.getStringExtra("MovieTitle");
        date = intent.getStringExtra("BookingDate");
        time = intent.getStringExtra("BookingTime");
        quantity = intent.getIntExtra("TicketQuantity", 1);
        seat = intent.getStringExtra("Seat");
        paymentType = intent.getStringExtra("PaymentType");
        totalPrice = intent.getDoubleExtra("TotalPrice", 0.0);
        movieCode = intent.getIntExtra("MovieCode", -1);

        // Set TextViews
        txtCustomer.setText(customerName);
        txtMovie.setText(movieTitle);
        txtDate.setText(date);
        txtTime.setText(time);
        txtQty.setText(String.valueOf(quantity));
        txtSeat.setText(seat);
        txtPayment.setText(paymentType);
        txtPrice.setText("RM " + String.format("%.2f", totalPrice));

        // Print button (optional)
        btnPrint.setOnClickListener(v ->
                Toast.makeText(ReceiptActivity.this, "Printing receipt...", Toast.LENGTH_SHORT).show());

        // Done button: close ReceiptActivity and go to MenuActivity
        btnDone.setOnClickListener(v -> {
            Intent intentMenu = new Intent(ReceiptActivity.this, MenuActivity.class);
            intentMenu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentMenu);
        });
    }
}
