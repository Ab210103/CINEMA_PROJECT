package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.adapter.BookingAdapter;
import com.example.cinema_project.model.Booking;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.BookingService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private BookingService bookingService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.historyrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bookingService = ApiUtils.getBookingService();

        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());

        if (!spm.isLoggedIn()) {
            Toast.makeText(getContext(),
                    "Please login to view booking history",
                    Toast.LENGTH_LONG).show();
            clearSessionAndRedirect();
            return;
        }

        loadBookingHistory(spm.getUser().getId(), spm.getToken());
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());
        if (spm.isLoggedIn()) {
            loadBookingHistory(spm.getUser().getId(), spm.getToken());
        }
    }

    private void loadBookingHistory(int userId, String apiKey) {

        bookingService.getAllBooking(apiKey).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<List<Booking>> call,
                                   @NonNull Response<List<Booking>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {

                    List<Booking> userBookings = new ArrayList<>();

                    // ✅ Filter bookings by logged user
                    for (Booking booking : response.body()) {
                        if (booking.getUser() != null &&
                                booking.getUser().getId() == userId) {
                            userBookings.add(booking);
                        }
                    }

                    if (userBookings.isEmpty()) {
                        Toast.makeText(getContext(),
                                "No booking history found",
                                Toast.LENGTH_SHORT).show();

                        bookingAdapter = new BookingAdapter(new ArrayList<>());
                        recyclerView.setAdapter(bookingAdapter);
                        return;
                    }

                    // ✅ Sort newest first
                    Collections.sort(userBookings, (b1, b2) ->
                            (b2.getDate() + b2.getTime())
                                    .compareTo(b1.getDate() + b1.getTime()));

                    bookingAdapter = new BookingAdapter(userBookings);
                    recyclerView.setAdapter(bookingAdapter);

                    DividerItemDecoration divider =
                            new DividerItemDecoration(getContext(),
                                    DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(divider);

                } else if (response.code() == 401) {
                    Toast.makeText(getContext(),
                            "Session expired. Please login again",
                            Toast.LENGTH_LONG).show();
                    clearSessionAndRedirect();
                } else {
                    Toast.makeText(getContext(),
                            "Failed to load booking history",
                            Toast.LENGTH_SHORT).show();
                    Log.e("HistoryFragment", response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Booking>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;

                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("HistoryFragment", t.toString());
            }
        });
    }

    private void clearSessionAndRedirect() {
        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());
        spm.logout();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
