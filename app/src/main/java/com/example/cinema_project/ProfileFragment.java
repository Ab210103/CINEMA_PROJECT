package com.example.cinema_project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.remote.ApiUtils;
import com.example.cinema_project.remote.CustService;
import com.example.cinema_project.sharedpref.SharedPrefManager;

public class ProfileFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvName, tvEmail, tvPhone, tvGender, tvProfession;
    private Button btnEdit, btnLogout;

    private CustService custService;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind Views
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvGender = view.findViewById(R.id.tvGender);
        tvProfession = view.findViewById(R.id.tvProfession);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        custService = ApiUtils.getCustService();

        refreshProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProfile();
    }

    private void refreshProfile() {
        SharedPrefManager spm = SharedPrefManager.getInstance(requireContext());

        if (spm.isLoggedIn()) {
            // User logged in
            Customer user = spm.getUser();

            btnEdit.setVisibility(View.VISIBLE);
            btnLogout.setText("Log Out");

            tvName.setText(user.getUsername() != null ? user.getUsername() : "-");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "-");
            tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "-");
            tvGender.setText(user.getGender() != null ? user.getGender() : "-");
            tvProfession.setText(user.getProfession() != null ? user.getProfession() : "-");

            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
            });

            btnLogout.setOnClickListener(v -> {
                spm.logout(); // Clear all user data
                Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

        } else {
            // Guest mode
            tvName.setText("Guest");
            tvEmail.setText("-");
            tvPhone.setText("-");
            tvGender.setText("-");
            tvProfession.setText("-");

            btnEdit.setVisibility(View.GONE);
            btnLogout.setText("Log In");

            btnLogout.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            });
        }
    }
}
