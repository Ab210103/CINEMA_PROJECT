package com.example.cinema_project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.model.Booking;
import com.example.cinema_project.adapter.HistoryAdapter;
import com.example.cinema_project.model.History;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.BookingService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private BookingService bookingService;

    private static final String API_KEY = "YOUR_API_KEY";

    // Session
    private boolean isLoggedIn = false;
    private int userId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            EdgeToEdge.enable(getActivity());
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.historyrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Init service using YOUR ApiUtils
        bookingService = ApiUtils.getBookingService();

        // Load session
        loadSession();

        // 🔐 Check login
        if (!isLoggedIn || userId == -1) {
            Toast.makeText(getContext(),
                    "Please login to view booking history",
                    Toast.LENGTH_LONG).show();

            recyclerView.setVisibility(View.GONE);
            return;
        }

        // Load booking history for this user
        loadBookingHistoryByUser();
    }

    // LOAD LOGIN SESSION
    private void loadSession() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        userId = prefs.getInt("userId", -1);

        Log.d("HistoryFragment",
                "Login=" + isLoggedIn + ", userId=" + userId);
    }

    // LOAD BOOKING HISTORY (FILTER BY USER)
    private void loadBookingHistoryByUser() {

        Call<List<Booking>> call =
                bookingService.getAllBooking(API_KEY);

        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(
                    @NonNull Call<List<Booking>> call,
                    @NonNull Response<List<Booking>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {

                    List<History> historyList = new ArrayList<>();

                    for (Booking b : response.body()) {

                        // 🔥 FILTER BY LOGGED IN USER
                        if (b.getUserid() == userId) {

                            History history = new History(
                                    b.getBId(),
                                    "Movie Code: " + b.getMcode(),
                                    b.getDate(),
                                    b.getTime()
                            );

                            historyList.add(history);
                        }
                    }

                    if (historyList.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No booking history found",
                                Toast.LENGTH_SHORT).show();
                    }

                    historyAdapter = new HistoryAdapter(historyList);
                    recyclerView.setAdapter(historyAdapter);

                } else {
                    Toast.makeText(getContext(),
                            "Failed to load booking history",
                            Toast.LENGTH_SHORT).show();

                    Log.e("HistoryFragment",
                            "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(
                    @NonNull Call<List<Booking>> call,
                    @NonNull Throwable t) {

                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();

                Log.e("HistoryFragment", "API Error", t);
            }
        });
    }
}
