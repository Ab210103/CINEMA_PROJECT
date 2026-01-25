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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private LinearLayout llBannerContainer;
    private RecyclerView rvHomeMovies;
    private Button btnLogin, btnSignUp;

    private List<Movie> movieList;

    // SharedPreferences constants
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

        // Check if user is logged in
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);

        if (isLoggedIn) {
            // Animate fade out and hide buttons
            fadeOutAndHide(btnLogin);
            fadeOutAndHide(btnSignUp);
        }

        // Button click listeners
        btnLogin.setOnClickListener(v -> startActivity(new Intent(getContext(), LoginActivity.class)));
        btnSignUp.setOnClickListener(v -> startActivity(new Intent(getContext(), SignUpActivity.class)));

        setupMoviePreview();

        return view;
    }

    private void setupMoviePreview() {
        movieList = new ArrayList<>();

        movieList.add(createMovie("Ejen Ali", R.drawable.movie1));
        movieList.add(createMovie("The Flash", R.drawable.movie2));
        movieList.add(createMovie("Transformers", R.drawable.movie3));
        movieList.add(createMovie("Spiderman: No Way Home", R.drawable.spider));
        movieList.add(createMovie("Black Panther : Wakanda Forever", R.drawable.bp));
        movieList.add(createMovie("Shazam!", R.drawable.shazam));

        MovieAdapter adapter = new MovieAdapter(
                getContext(),
                movieList,
                true,       // showBookNowButton
                false,      // useBannerImage
                movie -> Toast.makeText(getContext(),
                        "Clicked: " + movie.getTitle(),
                        Toast.LENGTH_SHORT).show()
        );

        rvHomeMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvHomeMovies.setAdapter(adapter);
    }

    private Movie createMovie(String title, int drawableRes) {
        Movie movie = new Movie();
        movie.setTitle(title);

        // Use drawable resource for demo purposes
        // movie.setImagePoster(Utils.drawableToByte(getContext(), drawableRes));

        return movie;
    }

    // Helper: fade out animation
    private void fadeOutAndHide(View view) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500); // 0.5 second
        animation.setFillAfter(true);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }
}
