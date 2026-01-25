package com.example.cinema_project.adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinema_project.R;
import com.example.cinema_project.model.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>{

    private List<History> history;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        private final TextView tvid1;
        private final TextView tvname;
        private final TextView tvdate;
        private final TextView tvtime;

        public ViewHolder(View view) {
            super(view);
            tvid1 = view.findViewById(R.id.tvid1);
            tvname = view.findViewById(R.id.tvproductname1);
            tvdate = view.findViewById(R.id.tvdate1);
            tvtime = view.findViewById(R.id.tvtime1);

            view.setOnCreateContextMenuListener(this);
        }

        public TextView getTvid1() { return tvid1; }
        public TextView getTvname() { return tvname; }
        public TextView getTvdate() { return tvdate; }
        public TextView getTvtime() { return tvtime; }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // Optional: add context menu items
            menu.setHeaderTitle("Select Action");
            menu.add(this.getAdapterPosition(), 121, 0, "Delete");
            menu.add(this.getAdapterPosition(), 122, 1, "Edit");
        }
    }

    public HistoryAdapter(List<History> products) {
        this.history = products;
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        History historys = history.get(position);
        holder.getTvid1().setText(String.valueOf(historys.getId()));
        holder.getTvname().setText(historys.getMoviename());
        holder.getTvdate().setText(historys.getDate());
        holder.getTvtime().setText(historys.getTime());
    }

    @Override
    public int getItemCount() {
        return history.size();
    }
}
