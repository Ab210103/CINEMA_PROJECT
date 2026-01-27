package com.example.cinema_project;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.target.Target;
import com.example.cinema_project.adapter.BannerAdapter;
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

    private RecyclerView rvBanner, rvHomeMovies;
    private Button btnLogin, btnSignUp;
    private List<Movie> movieList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvBanner = view.findViewById(R.id.rvBanner);
        rvHomeMovies = view.findViewById(R.id.rvHomeMovies);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignUp = view.findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(getContext(), LoginActivity.class)));

        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SignUpActivity.class)));

        checkLoginStatus();
        fetchMoviesFromBackend();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
        fetchMoviesFromBackend(); // 🔥 Refresh bila balik ke fragment
    }

    private void checkLoginStatus() {
        boolean loggedIn = SharedPrefManager.getInstance(requireContext()).isLoggedIn();

        if (loggedIn) {
            btnLogin.setVisibility(View.GONE);
            btnSignUp.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
        }
    }

    private void fetchMoviesFromBackend() {
        MovieService movieService = ApiUtils.getMovieService();
        boolean isLoggedIn = SharedPrefManager.getInstance(requireContext()).isLoggedIn();

        String token = isLoggedIn
                ? SharedPrefManager.getInstance(requireContext()).getToken()
                : "1cd4b43d-e4e1-4920-9805-cc3f6826d969"; // public token

        Call<List<Movie>> call = movieService.getAllMovie(token);

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    movieList = response.body();

                    // 🔗 Full URL for images
                    for (Movie m : movieList) {
                        if (m.getImageBanner() != null && !m.getImageBanner().startsWith("http")) {
                            m.setImageBanner(ApiUtils.UPLOADS_URL + m.getImageBanner());
                        }
                        if (m.getImagePoster() != null && !m.getImagePoster().startsWith("http")) {
                            m.setImagePoster(ApiUtils.UPLOADS_URL + m.getImagePoster());
                        }
                    }

                    setupBanner(movieList, isLoggedIn);
                    setupMovieGrid(movieList, isLoggedIn);

                } else {
                    rvBanner.setAdapter(null);
                    rvHomeMovies.setAdapter(null);
                    Toast.makeText(getContext(), "No movies found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBanner(List<Movie> movies, boolean isLoggedIn) {
        rvBanner.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), movies, movie -> {
            if (!isLoggedIn) {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(getContext(), DetailsActivity.class);
            i.putExtra("movieId", movie.getId());
            i.putExtra("title", movie.getTitle());
            startActivity(i);
        });

        rvBanner.setAdapter(bannerAdapter);
    }

    private void setupMovieGrid(List<Movie> movies, boolean isLoggedIn) {
        MovieAdapter adapter = new MovieAdapter(
                getContext(),
                movies,
                isLoggedIn,
                R.layout.item_movie1,
                R.id.imgMovie,
                R.id.tvMovieTitle,
                0,
                movie -> {
                    if (!isLoggedIn) {
                        Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent i = new Intent(getContext(), DetailsActivity.class);
                    i.putExtra("movieId", movie.getId());
                    i.putExtra("title", movie.getTitle());
                    startActivity(i);
                }
        );

        rvHomeMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvHomeMovies.setAdapter(adapter);
    }

    // 🔧 Glide helper (optional)
    public static void loadMovieImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        Log.e("Glide", "Failed to load: " + url, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        Log.d("Glide", "Loaded: " + url);
                        return false;
                    }
                })
                .into(imageView);
    }
}
