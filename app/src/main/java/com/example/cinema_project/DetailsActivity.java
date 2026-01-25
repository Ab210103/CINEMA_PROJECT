package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imgPoster;
    private TextView tvTitle, tvRating, tvSeat;
    private TextView tvPricePerTicket, tvTotalPrice;
    private Spinner spDate, spTime, spTicketQty;
    private GridLayout gridSeats;
    private RadioGroup rgPayment;
    private Button btnConfirm;

    private List<String> selectedSeats = new ArrayList<>();
    private int maxTickets = 1;

    private final double PRICE_PER_TICKET = 15.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        imgPoster = findViewById(R.id.imgPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvRating = findViewById(R.id.tvRating);
        tvSeat = findViewById(R.id.tvSeat);
        tvPricePerTicket = findViewById(R.id.tvPricePerTicket);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        spDate = findViewById(R.id.spDate);
        spTime = findViewById(R.id.spTime);
        spTicketQty = findViewById(R.id.spTicketQty);
        gridSeats = findViewById(R.id.gridSeats);
        rgPayment = findViewById(R.id.rgPayment);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Intent data
        Intent intent = getIntent();
        tvTitle.setText(intent.getStringExtra("title"));
        tvRating.setText("⭐ " + intent.getStringExtra("rating"));
        imgPoster.setImageResource(
                intent.getIntExtra("poster", R.drawable.ic_launcher_background)
        );

        // Spinner setup (WHITE TEXT)
        setupSpinner(spDate, new String[]{"2026-01-19", "2026-01-20", "2026-01-21"});
        setupSpinner(spTime, new String[]{"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"});
        setupSpinner(spTicketQty, new Integer[]{1,2,3,4,5,6,7,8,9,10});

        // Price init
        tvPricePerTicket.setText("RM " + String.format("%.2f", PRICE_PER_TICKET));
        updateTotalPrice();

        spTicketQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxTickets = (int) spTicketQty.getSelectedItem();

                while (selectedSeats.size() > maxTickets) {
                    selectedSeats.remove(selectedSeats.size() - 1);
                }

                updateSeatText();
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupSeats();

        btnConfirm.setOnClickListener(v -> {
            if (selectedSeats.size() != maxTickets) {
                Toast.makeText(this,
                        "Please select " + maxTickets + " seats",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (rgPayment.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this,
                        "Please select payment method",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this,
                    "Booking Confirmed\nTotal: RM " +
                            String.format("%.2f", PRICE_PER_TICKET * maxTickets),
                    Toast.LENGTH_LONG).show();
        });
    }

    // ---------------- SPINNER (WHITE TEXT) ----------------

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item_white, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(adapter);
    }

    private void setupSpinner(Spinner spinner, Integer[] items) {
        ArrayAdapter<Integer> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_item_white, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(adapter);
    }

    // ---------------- SEATS ----------------

    private void setupSeats() {
        String rows = "ABCDEFGH";
        int columns = 8;

        gridSeats.removeAllViews();
        gridSeats.setColumnCount(columns);

        for (int r = 0; r < rows.length(); r++) {
            for (int c = 1; c <= columns; c++) {

                final String seatName = rows.charAt(r) + String.valueOf(c);

                ToggleButton seat = new ToggleButton(this);
                seat.setText(seatName);
                seat.setTextOn(seatName);
                seat.setTextOff(seatName);
                seat.setTextSize(10);

                int size = dpToPx(38);
                GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                p.width = size;
                p.height = size;
                p.setMargins(4,4,4,4);
                seat.setLayoutParams(p);

                seat.setOnClickListener(v -> {
                    if (seat.isChecked()) {
                        if (selectedSeats.size() >= maxTickets) {
                            seat.setChecked(false);
                            Toast.makeText(this,
                                    "Max " + maxTickets + " seats",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            selectedSeats.add(seatName);
                        }
                    } else {
                        selectedSeats.remove(seatName);
                    }
                    updateSeatText();
                });

                gridSeats.addView(seat);
            }
        }
    }

    private void updateSeatText() {
        tvSeat.setText(
                selectedSeats.isEmpty()
                        ? "Seat: -"
                        : "Seat: " + String.join(", ", selectedSeats)
        );
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText(
                "RM " + String.format("%.2f", PRICE_PER_TICKET * maxTickets)
        );
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
