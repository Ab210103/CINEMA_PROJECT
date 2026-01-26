package com.example.cinema_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class HomeFragment extends Fragment {

    private LinearLayout llBannerContainer;
    private RecyclerView rvHomeMovies;
    private Button btnLogin, btnSignUp;

    private List<Movie> movieList;

    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        llBannerContainer = view.findViewById(R.id.llBannerContainer);
        rvHomeMovies = view.findViewById(R.id.rvHomeMovies);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignUp = view.findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        btnSignUp.setOnClickListener(v -> startActivity(new Intent(getContext(), SignUpActivity.class)));

        checkLoginStatus();

        fetchMoviesFromBackend();

        return view;
    }

    private void checkLoginStatus() {
        SharedPrefManager spm = new SharedPrefManager(requireContext());

        if (spm.isLoggedIn()) {
            fadeOutAndHide(btnLogin);
            fadeOutAndHide(btnSignUp);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void fetchMoviesFromBackend() {
        MovieService movieService = ApiUtils.getMovieService(); // Retrofit
        Call<List<Movie>> call = movieService.getAllMovie("YOUR_API_KEY_HERE");

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (!isAdded()) return; // fragment not attached

                if (response.isSuccessful() && response.body() != null) {
                    movieList = response.body();
                    setupMovieRecycler(movieList);
                } else {
                    Toast.makeText(requireContext(), "Failed to load movies: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                if (!isAdded()) return; // fragment not attached
                Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupMovieRecycler(List<Movie> movies) {
        MovieAdapter adapter = new MovieAdapter(
                getContext(),
                movies,
                false,
                R.layout.item_movie1,
                R.id.imgMovie,
                R.id.tvMovieTitle,
                0,
                movie -> Toast.makeText(getContext(), "Clicked: " + movie.getTitle(), Toast.LENGTH_SHORT).show()
        );

        rvHomeMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvHomeMovies.setAdapter(adapter);
    }

    private void fadeOutAndHide(View view) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }

    // Call this method from parent activity after logout
    public void showLoginButtons() {
        btnLogin.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.VISIBLE);
    }
}
