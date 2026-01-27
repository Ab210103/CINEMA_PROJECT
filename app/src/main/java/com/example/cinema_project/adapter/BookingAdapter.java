package com.example.cinema_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.R;
import com.example.cinema_project.model.Booking;
import com.example.cinema_project.model.Movie;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingAdapter.ViewHolder holder,
                                 int position) {

        Booking booking = bookingList.get(position);

        holder.tvBookingId.setText("Booking #" + booking.getBId());
        holder.tvDate.setText("Date: " + booking.getDate());
        holder.tvTime.setText("Time: " + booking.getTime());
        holder.tvSeat.setText("Seat: " + booking.getSeat());
        holder.tvQuantity.setText("Tickets: " + booking.getQuantity());
        holder.tvPayment.setText("Payment: " + booking.getTypepayment());
        holder.tvTotal.setText("RM " + String.format("%.2f", booking.getTotal()));

        // 🎬 Movie Info
        Movie movie = booking.getMovie_code();
        if (movie != null) {
            holder.tvNameMovie.setText("Movie: " + movie.getTitle());
        } else {
            holder.tvNameMovie.setText("Movie: Unknown");
        }

        // 🖱️ Click on card
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    "Booking ID: " + booking.getBId(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBookingId, tvTotal, tvNameMovie,
                tvDate, tvTime, tvSeat, tvQuantity, tvPayment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvTotal     = itemView.findViewById(R.id.tvTotal);
            tvNameMovie = itemView.findViewById(R.id.tvnamemovie);
            tvDate      = itemView.findViewById(R.id.tvDate);
            tvTime      = itemView.findViewById(R.id.tvTime);
            tvSeat      = itemView.findViewById(R.id.tvSeat);
            tvQuantity  = itemView.findViewById(R.id.tvQuantity);
            tvPayment   = itemView.findViewById(R.id.tvPayment);
        }
    }
}
