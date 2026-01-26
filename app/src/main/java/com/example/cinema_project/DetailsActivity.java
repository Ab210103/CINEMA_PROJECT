package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.model.RegisterResponse;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.BookingService;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imgPoster;
    private TextView tvTitle, tvSeat;
    private TextView tvPricePerTicket, tvTotalPrice;
    private Spinner spDate, spTime, spTicketQty;
    private GridLayout gridSeats;
    private RadioGroup rgPayment;
    private Button btnConfirm;

    private List<String> selectedSeats = new ArrayList<>();
    private int maxTickets = 1;
    private final double PRICE_PER_TICKET = 15.00;
    private int movieCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        imgPoster = findViewById(R.id.imgPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvSeat = findViewById(R.id.tvSeat);
        tvPricePerTicket = findViewById(R.id.tvPricePerTicket);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        spDate = findViewById(R.id.spDate);
        spTime = findViewById(R.id.spTime);
        spTicketQty = findViewById(R.id.spTicketQty);
        gridSeats = findViewById(R.id.gridSeats);
        rgPayment = findViewById(R.id.rgPayment);
        btnConfirm = findViewById(R.id.btnConfirm);

        movieCode = getIntent().getIntExtra("moviecode", 0);

        fetchMovieDetails(movieCode);

        setupDateSpinner();
        setupSpinner(spTime, new String[]{"10:00 AM","12:30 PM","3:00 PM","5:30 PM","7:00 PM","9:30 PM"});
        setupSpinner(spTicketQty, new Integer[]{1,2,3,4,5,6,7,8,9,10});

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
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupSeats();
        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    // ================= FETCH MOVIE =================
    private void fetchMovieDetails(int movieCode) {
        MovieService movieService = ApiUtils.getMovieService();
        Call<Movie> call = movieService.getMovie("YOUR_API_KEY_HERE", movieCode);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();
                    tvTitle.setText(movie.getTitle());

                    String imageUrl = "http://10.0.2.2/uploads/" + movie.getImagePoster();

                    Glide.with(DetailsActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imgPoster);

                } else {
                    Toast.makeText(DetailsActivity.this, "Failed to load movie", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= DATE SPINNER =================
    private void setupDateSpinner() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String[] dates = new String[7];
        for (int i = 0; i < 7; i++) {
            dates[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        setupSpinner(spDate, dates);
    }

    private void setupSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(adapter);
    }

    private void setupSpinner(Spinner spinner, Integer[] items) {
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_item_white, items);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_white);
        spinner.setAdapter(adapter);
    }

    // ================= SEATS =================
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
                            Toast.makeText(this, "Max " + maxTickets + " seats", Toast.LENGTH_SHORT).show();
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
        tvSeat.setText(selectedSeats.isEmpty() ? "Seat: -" : "Seat: " + String.join(", ", selectedSeats));
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText("RM " + String.format("%.2f", PRICE_PER_TICKET * maxTickets));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ================= CONFIRM =================
    private void confirmBooking() {
        if (selectedSeats.size() != maxTickets) {
            Toast.makeText(this, "Please select " + maxTickets + " seats", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPaymentId = rgPayment.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedPayment = findViewById(selectedPaymentId);
        String paymentType = selectedPayment.getText().toString();

        String date = spDate.getSelectedItem().toString();
        String time = spTime.getSelectedItem().toString();
        double totalPrice = PRICE_PER_TICKET * maxTickets;

        SharedPrefManager spm = new SharedPrefManager(getApplicationContext());
        int userId = spm.getUser().getId();

        BookingService bookingService = ApiUtils.getBookingService();
        Call<RegisterResponse> call = bookingService.addBooking(
                "YOUR_API_KEY_HERE",
                date, time, maxTickets, paymentType, totalPrice, userId, movieCode
        );

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetailsActivity.this, "Booking Successful!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DetailsActivity.this, ReceiptActivity.class);
                    intent.putExtra("title", tvTitle.getText().toString());
                    intent.putExtra("seats", String.join(", ", selectedSeats));
                    intent.putExtra("date", date);
                    intent.putExtra("time", time);
                    intent.putExtra("totalPrice", totalPrice);
                    intent.putExtra("paymentType", paymentType);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DetailsActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(DetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
