package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.cinema_project.model.Booking;
import com.example.cinema_project.model.Customer;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.BookingService;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {

    private ImageView imgPoster;
    private TextView tvTitle, tvSeat, tvPricePerTicket, tvTotalPrice;
    private Spinner spDate, spTime, spTicketQty;
    private GridLayout gridSeats;
    private RadioGroup rgPayment;
    private Button btnConfirm;

    private List<String> selectedSeats = new ArrayList<>();
    private int maxTickets = 1;
    private final double PRICE_PER_TICKET = 15.00;
    private int movieCode;
    private String movieTitle;

    private MovieService movieService;
    private BookingService bookingService;
    private String apiKey;
    private Customer currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Toolbar toolbar = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // 🔹 Initialize views
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

        // 🔹 Get movie code from MovieFragment
        movieCode = getIntent().getIntExtra("moviecode", -1);
        if (movieCode == -1) {
            Toast.makeText(this, "Movie not selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 🔹 Get token & current user
        SharedPrefManager spm = SharedPrefManager.getInstance(this);
        apiKey = spm.getToken();
        currentUser = spm.getUser();
        if (apiKey == null || apiKey.isEmpty() || currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        movieService = ApiUtils.getMovieService();
        bookingService = ApiUtils.getBookingService();

        fetchMovieDetails(movieCode);
        setupDateSpinner();
        setupSpinner(spTime, new String[]{"10:00 AM","12:30 PM","3:00 PM","5:30 PM","7:00 PM","9:30 PM"});
        setupSpinner(spTicketQty, new Integer[]{1,2,3,4,5,6,7,8,9,10});

        tvPricePerTicket.setText("RM " + String.format("%.2f", PRICE_PER_TICKET));
        updateTotalPrice();

        spTicketQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                maxTickets = (int) spTicketQty.getSelectedItem();
                while (selectedSeats.size() > maxTickets) selectedSeats.remove(selectedSeats.size() - 1);
                updateSeatText();
                updateTotalPrice();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        setupSeats();

        // 🔹 Confirm booking button
        btnConfirm.setOnClickListener(v -> confirmBooking());
    }

    private void fetchMovieDetails(int movieCode) {
        movieService.getMovie(apiKey, movieCode).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Movie movie = response.body();

                    movieTitle = movie.getTitle(); // ⭐ INI PENTING
                    tvTitle.setText(movieTitle);

                    String posterUrl = movie.getImageBanner() != null
                            ? ApiUtils.UPLOADS_URL + movie.getImageBanner()
                            : "";

                    Glide.with(DetailsActivity.this)
                            .load(posterUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imgPoster);
                } else {
                    Toast.makeText(DetailsActivity.this,
                            "Failed to load movie",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(DetailsActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


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
                p.width = size; p.height = size;
                p.setMargins(4,4,4,4);
                seat.setLayoutParams(p);

                seat.setOnClickListener(v -> {
                    if (seat.isChecked()) {
                        if (selectedSeats.size() >= maxTickets) seat.setChecked(false);
                        else selectedSeats.add(seatName);
                    } else selectedSeats.remove(seatName);
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

    // 🔹 Confirm booking function with DB save
    private void confirmBooking() {

        if (selectedSeats.size() != maxTickets) {
            Toast.makeText(this,
                    "Please select " + maxTickets + " seats",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPaymentId = rgPayment.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this,
                    "Please select payment method",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedPaymentId);
        String paymentType = rb.getText().toString(); // CARD / ONLINE BANKING / E-WALLET

        String date = spDate.getSelectedItem().toString();
        String time = spTime.getSelectedItem().toString();
        double totalPrice = PRICE_PER_TICKET * maxTickets;
        String seatString = String.join(",", selectedSeats);
        int userId = currentUser.getId();

        bookingService.addBooking(
                apiKey,
                date,
                time,
                maxTickets,
                seatString,
                paymentType,
                totalPrice,
                userId,
                movieCode
        ).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String serverResponse = response.body().string();
                        Log.d("BOOKING_RESPONSE", serverResponse);

                        Toast.makeText(DetailsActivity.this,
                                "Booking Successful!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                DetailsActivity.this,
                                ReceiptActivity.class
                        );

                        intent.putExtra("MovieTitle", movieTitle); // ⭐ WAJIB
                        intent.putExtra("Seat", seatString);
                        intent.putExtra("TotalPrice", totalPrice);
                        intent.putExtra("PaymentType", paymentType);
                        intent.putExtra("TicketQuantity", maxTickets);
                        intent.putExtra("BookingDate", date);
                        intent.putExtra("BookingTime", time);

                        startActivity(intent);
                        finish();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this,
                            "Booking failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("BOOKING_ERROR", t.toString());
                Toast.makeText(DetailsActivity.this,
                        "Network Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
