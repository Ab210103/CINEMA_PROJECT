package com.example.cinema_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.R;
import com.example.cinema_project.model.Movie;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    /**
     * ViewHolder class for movie item
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvId;
        public TextView tvTitle;
        public TextView tvStaffId;

        public ViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvIdValue);
            tvTitle = itemView.findViewById(R.id.tvMovieValue);
            tvStaffId = itemView.findViewById(R.id.tvStaffValue);

            itemView.setOnLongClickListener(this); // track long-press
        }

        @Override
        public boolean onLongClick(View v) {
            currentPos = getAdapterPosition(); // record the long-pressed item
            return false; // false so context menu can appear
        }
    }

    // Adapter fields
    private List<Movie> movieList;
    private Context mContext;
    private int currentPos = -1; // currently selected item via long press

    public ListAdapter(Context context, List<Movie> movies) {
        mContext = context;
        movieList = movies;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate staff layout for movie list item
        View view = inflater.inflate(R.layout.item_list_staff_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        holder.tvId.setText(String.valueOf(movie.getId()));
        holder.tvTitle.setText(movie.getTitle());

        // show staff ID instead of staff name
        holder.tvStaffId.setText(String.valueOf(movie.getStaffID()));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /** Return the currently long-pressed / selected movie */
    public Movie getSelectedItem() {
        if (currentPos >= 0 && movieList != null && currentPos < movieList.size()) {
            return movieList.get(currentPos);
        }
        return null;
    }

    /** Optional: clear selection */
    public void clearSelection() {
        currentPos = -1;
    }

    /** Optional: update list dynamically */
    public void updateList(List<Movie> newList) {
        movieList = newList;
        notifyDataSetChanged();
    }
}
