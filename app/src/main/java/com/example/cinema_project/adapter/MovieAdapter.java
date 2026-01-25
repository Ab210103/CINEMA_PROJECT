package com.example.cinema_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinema_project.R;
import com.example.cinema_project.model.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onClick(Movie movie);
    }

    private Context context;
    private List<Movie> movieList;
    private boolean showBookNowButton;
    private boolean useBannerImage;
    private OnMovieClickListener listener;

    public MovieAdapter(Context context, List<Movie> movieList, boolean showBookNowButton,
                        boolean useBannerImage, OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.showBookNowButton = showBookNowButton;
        this.useBannerImage = useBannerImage;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvTitle.setText(movie.getTitle());

        // Gunakan Glide untuk load image (poster atau banner)
        int resId = useBannerImage ? R.drawable.ic_launcher_background : R.drawable.ic_launcher_background; // default fallback
        Glide.with(context)
                .load(movie.getImagePoster() != null ? movie.getImagePoster() : resId)
                .centerCrop()
                .into(holder.imgMovie);

        // Show / hide Book Now button
        holder.btnBookNow.setVisibility(showBookNowButton ? View.VISIBLE : View.GONE);

        // Click listeners
        holder.itemView.setOnClickListener(v -> { if(listener != null) listener.onClick(movie); });
        holder.imgMovie.setOnClickListener(v -> { if(listener != null) listener.onClick(movie); });
        holder.btnBookNow.setOnClickListener(v -> { if(listener != null) listener.onClick(movie); });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvTitle;
        Button btnBookNow;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}
