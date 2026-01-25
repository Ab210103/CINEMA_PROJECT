package com.example.cinema_project;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.List;

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

        // Buat list movie
        movieList = new ArrayList<>();
        movieList.add(createMovie("Avengers: Endgame", R.drawable.marvel));
        movieList.add(createMovie("The Batman", R.drawable.batman));
        movieList.add(createMovie("Spiderman: No Way Home", R.drawable.spider));
        movieList.add(createMovie("Guardians of Galaxy 3", R.drawable.gog));
        movieList.add(createMovie("Shazam!", R.drawable.shazam));
        movieList.add(createMovie("Black Panther: Wakanda Forever", R.drawable.bp));

        // Buat adapter
        MovieAdapter adapter = new MovieAdapter(
                getContext(),
                movieList,
                true,   // showBookNowButton = true untuk MovieFragment
                false,  // useBannerImage = false (pakai imagePoster)
                movie -> {
                    // Click listener
                    Intent intent = new Intent(getContext(), DetailsActivity.class);
                    intent.putExtra("title", movie.getTitle());
                    if (movie.getImagePoster() != null) {
                        intent.putExtra("imagePoster", movie.getImagePoster());
                    }
                    startActivity(intent);
                }
        );

        // Grid layout: 2 columns
        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMovies.setAdapter(adapter);
    }

    // --- Helper function untuk create Movie object dengan title + drawable ---
    private Movie createMovie(String title, int drawableRes) {
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setImagePoster(Utils.drawableToByte(getContext(), drawableRes));
        return movie;
    }
}
