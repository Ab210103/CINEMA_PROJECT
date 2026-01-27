package com.example.cinema_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.adapter.MovieAdapter;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.MovieService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {

    private RecyclerView rvMovies;
    private List<Movie> movieList;

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
        MovieService movieService = ApiUtils.getMovieService();
        boolean isLoggedIn = SharedPrefManager.getInstance(requireContext()).isLoggedIn();

        // Use token if logged in, otherwise fallback token
        String apiKey = isLoggedIn ? SharedPrefManager.getInstance(requireContext()).getToken() : null;
        String tetoken = "1cd4b43d-e4e1-4920-9805-cc3f6826d969";

        Call<List<Movie>> call;
        if (apiKey != null) {
            call = movieService.getAllMovie(apiKey);
        } else {
            call = movieService.getAllMovie(tetoken);
        }

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    movieList = response.body();

                    // Append full URL for banner & poster
                    for (Movie m : movieList) {
                        if (m.getImageBanner() != null) {
                            m.setImageBanner(ApiUtils.UPLOADS_URL + m.getImageBanner());
                        }
                        if (m.getImagePoster() != null) {
                            m.setImagePoster(ApiUtils.UPLOADS_URL + m.getImagePoster());
                        }
                    }

                    setupMovieGrid(movieList, isLoggedIn);
                } else {
                    Log.w("MovieFragment", "No movies found or empty response");
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                t.printStackTrace();
                Log.e("MovieFragment", "API call failed: " + t.getMessage());
            }
        });
    }

    private void setupMovieGrid(List<Movie> movies, boolean isLoggedIn) {
        MovieAdapter adapter = new MovieAdapter(
                getContext(),
                movies,
                isLoggedIn,
                R.layout.item_movie,
                R.id.imgPoster,
                R.id.tvTitle,
                R.id.btnBookNow,
                movie -> {
                    if (!isLoggedIn) {
                        showLoginDialog();
                        return;
                    }

                    // ✅ Pass moviecode key correctly to DetailsActivity
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("moviecode", movie.getId()); // must match DetailsActivity
                    intent.putExtra("title", movie.getTitle());
                    intent.putExtra("poster", movie.getImagePoster()); // optional if you want to display poster directly
                    startActivity(intent);
                }
        );

        rvMovies.setAdapter(adapter);
    }

    // Check if user is logged in
    private boolean isUserLoggedIn() {
        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());
        return spm.isLoggedIn();
    }

    // Show login dialog if user not logged in
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
