package com.example.cinema_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cinema_project.adapter.HistoryAdapter;
import com.example.cinema_project.model.History;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    private HistoryAdapter historyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Edge-to-edge must use the hosting activity
        if (getActivity() != null) {
            EdgeToEdge.enable(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Prepare dummy data
        List<History> historyList = fetchAllContactRecords();
        historyAdapter = new HistoryAdapter(historyList);

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.historyrecycler);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private List<History> fetchAllContactRecords() {
        List<History> histories = new ArrayList<>();
        histories.add(new History(1, "AVATAR", "12/1/2025", "10:35"));
        histories.add(new History(2, "BLACK PANTHER", "10/3/2025", "12:47"));
        histories.add(new History(3, "PAPA ZOLA: THE MOVIE", "21/4/2025", "15:55"));
        histories.add(new History(4, "TRANSFORMERS", "20/5/2025", "19:21"));

        return histories;
    }
}