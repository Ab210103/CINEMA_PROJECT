package com.example.cinema_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.adapter.BookingAdapter;
import com.example.cinema_project.model.Booking;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.BookingService;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private BookingService bookingService;
    private MovieService movieService;

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
        movieService = ApiUtils.getMovieService();

        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());

        if (!spm.isLoggedIn()) {
            Toast.makeText(getContext(),
                    "Please login to view booking history",
                    Toast.LENGTH_LONG).show();
            return;
        }

        loadBookingHistory(spm.getUser().getId(), spm.getToken());
    }

    private void loadBookingHistory(int userId, String apiKey) {

        bookingService.getAllBooking(apiKey).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<List<Booking>> call,
                                   @NonNull Response<List<Booking>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {

                    List<Booking> userBookings = new ArrayList<>();
                    Map<Integer, Movie> movieMap = new HashMap<>();

                    // Filter bookings for the current user
                    for (Booking booking : response.body()) {
                        if (booking.getUserid() == userId) {
                            userBookings.add(booking);
                        }
                    }

                    if (userBookings.isEmpty()) {
                        Toast.makeText(getContext(), "No booking history found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get unique movie codes
                    List<Integer> uniqueMovieCodes = new ArrayList<>();
                    for (Booking booking : userBookings) {
                        if (!uniqueMovieCodes.contains(booking.getMcode())) {
                            uniqueMovieCodes.add(booking.getMcode());
                        }
                    }

                    // Track how many movies fetched
                    int totalMovies = uniqueMovieCodes.size();
                    int[] fetchedCount = {0};

                    // Fetch each movie detail
                    for (int code : uniqueMovieCodes) {
                        movieService.getMovie(apiKey, code).enqueue(new Callback<Movie>() {
                            @Override
                            public void onResponse(Call<Movie> call, Response<Movie> movieResponse) {
                                if (movieResponse.isSuccessful() && movieResponse.body() != null) {
                                    movieMap.put(code, movieResponse.body());
                                } else {
                                    movieMap.put(code, null); // fallback
                                }

                                fetchedCount[0]++;
                                if (fetchedCount[0] == totalMovies) {
                                    // All movies fetched, set adapter
                                    bookingAdapter = new BookingAdapter(userBookings, movieMap);
                                    recyclerView.setAdapter(bookingAdapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<Movie> call, Throwable t) {
                                movieMap.put(code, null);
                                fetchedCount[0]++;
                                if (fetchedCount[0] == totalMovies) {
                                    bookingAdapter = new BookingAdapter(userBookings, movieMap);
                                    recyclerView.setAdapter(bookingAdapter);
                                }
                            }
                        });
                    }

                } else {
                    Toast.makeText(getContext(), "Failed to load booking history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Booking>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
