package com.example.cinema_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.R;
import com.example.cinema_project.model.Booking;
import com.example.cinema_project.model.Movie;

import java.util.List;
import java.util.Map;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<Booking> bookingList;
    private final Map<Integer, Movie> movieMap; // movie_code -> Movie

    public BookingAdapter(List<Booking> bookingList, Map<Integer, Movie> movieMap) {
        this.bookingList = bookingList;
        this.movieMap = movieMap;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        Movie movie = movieMap.get(booking.getMcode());

        holder.tvid1.setText(String.valueOf(booking.getBId()));
        holder.tvproductname1.setText(movie != null ? movie.getTitle() : "Unknown Movie");
        holder.tvdate1.setText(booking.getDate());
        holder.tvtime1.setText(booking.getTime());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvid1, tvproductname1, tvdate1, tvtime1;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvid1 = itemView.findViewById(R.id.tvid1);
            tvproductname1 = itemView.findViewById(R.id.tvproductname1);
            tvdate1 = itemView.findViewById(R.id.tvdate1);
            tvtime1 = itemView.findViewById(R.id.tvtime1);
        }
    }
}
