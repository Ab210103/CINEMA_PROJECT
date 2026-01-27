package com.example.cinema_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinema_project.R;
import com.example.cinema_project.model.Movie;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    public interface BannerClickListener {
        void onBookNowClick(Movie movie);
    }

    private Context context;
    private List<Movie> bannerList;
    private BannerClickListener listener;

    public BannerAdapter(Context context, List<Movie> bannerList, BannerClickListener listener) {
        this.context = context;
        this.bannerList = bannerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = bannerList.get(position);

        if (movie.getImageBanner() != null && !movie.getImageBanner().isEmpty()) {
            Glide.with(context)
                    .load(movie.getImageBanner())  // pakai getter getImageBanner()
                    .centerCrop()
                    .into(holder.imgBanner);
        } else {
            holder.imgBanner.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.btnBookNow.setOnClickListener(v -> {
            if (listener != null) listener.onBookNowClick(movie);
        });
    }



    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;
        Button btnBookNow;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}
