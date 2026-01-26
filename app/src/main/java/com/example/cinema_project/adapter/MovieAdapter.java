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

    // Click listener interface
    public interface OnMovieClickListener {
        void onClick(Movie movie);
    }

    private Context context;
    private List<Movie> movieList;
    private boolean showBookNowButton;
    private int layoutResId;
    private int imgViewId, tvTitleId, btnBookNowId;
    private OnMovieClickListener listener;

    private int selectedPosition = -1; // track long-pressed item

    public MovieAdapter(Context context,
                        List<Movie> movieList,
                        boolean showBookNowButton,
                        int layoutResId,
                        int imgViewId,
                        int tvTitleId,
                        int btnBookNowId,
                        OnMovieClickListener listener) {
        this.context = context;
        this.movieList = movieList;
        this.showBookNowButton = showBookNowButton;
        this.layoutResId = layoutResId;
        this.imgViewId = imgViewId;
        this.tvTitleId = tvTitleId;
        this.btnBookNowId = btnBookNowId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvTitle.setText(movie.getTitle());

        // Load imagePoster (byte[] or fallback drawable)
        if (movie.getImagePoster() != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(movie.getImagePoster())
                    .centerCrop()
                    .into(holder.imgMovie);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.imgMovie);
        }

        // Show/hide Book Now button
        if (holder.btnBookNow != null)
            holder.btnBookNow.setVisibility(showBookNowButton ? View.VISIBLE : View.GONE);

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(movie);
        });

        holder.imgMovie.setOnClickListener(v -> {
            if (listener != null) listener.onClick(movie);
        });

        if (holder.btnBookNow != null)
            holder.btnBookNow.setOnClickListener(v -> {
                if (listener != null) listener.onClick(movie);
            });

        // Long press for context menu
        holder.itemView.setOnLongClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            return false; // return false supaya context menu muncul
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // ViewHolder
    class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvTitle;
        Button btnBookNow;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(imgViewId);
            tvTitle = itemView.findViewById(tvTitleId);
            btnBookNow = btnBookNowId != 0 ? itemView.findViewById(btnBookNowId) : null;
        }
    }

    // return currently selected movie (long press)
    public Movie getSelectedItem() {
        if (selectedPosition >= 0 && movieList != null && selectedPosition < movieList.size()) {
            return movieList.get(selectedPosition);
        }
        return null;
    }

    // Optional: Update movie list dynamically
    public void updateList(List<Movie> newList) {
        movieList = newList;
        notifyDataSetChanged();
    }
}
