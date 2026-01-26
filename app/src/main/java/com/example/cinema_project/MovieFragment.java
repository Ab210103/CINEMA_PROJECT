package com.example.cinema_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cinema_project.adapter.MovieAdapter;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    private RecyclerView rvMovies;

    public MovieFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        rvMovies = view.findViewById(R.id.rvMovies);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fetchMoviesFromApi();
    }

    private void fetchMoviesFromApi() {
        MovieService apiService = ApiUtils.getMovieService();
        Call<List<Movie>> call = apiService.getAllMovie("YOUR_API_KEY_HERE");

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movieList = response.body();

                    MovieAdapter adapter = new MovieAdapter(
                            getContext(),
                            movieList,
                            true,
                            R.layout.item_movie,
                            R.id.imgPoster,
                            R.id.tvTitle,
                            R.id.btnBookNow,
                            movie -> {

                                // 🔐 CHECK LOGIN STATUS
                                if (!isUserLoggedIn()) {
                                    showLoginDialog();
                                    return;
                                }

                                // ✅ Kalau dah login → terus ke Booking
                                Intent intent = new Intent(getContext(), DetailsActivity.class);
                                intent.putExtra("movieId", movie.getId());
                                intent.putExtra("title", movie.getTitle());
                                startActivity(intent);
                            }
                    );

                    rvMovies.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // 🔹 Check login dari SharedPreferences
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return prefs.getBoolean("isLoggedIn", false);
    }

    // 🔹 Pop-up suruh login dulu
    private void showLoginDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("Please login first before booking a movie.")
                .setPositiveButton("Login", (dialog, which) -> {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
