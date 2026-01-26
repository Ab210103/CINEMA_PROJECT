package com.example.cinema_project.sharedpref;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cinema_project.model.Customer;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "cinema_shared_pref";

    private static final String KEY_ID = "key_id";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_ROLE = "key_role";
    private static final String KEY_GENDER = "key_gender";
    private static final String KEY_PROFESSION = "key_profession";
    private static final String KEY_PHONE = "key_phone";

    private static SharedPrefManager mInstance;
    private final Context mCtx;

    public SharedPrefManager(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void storeUser(Customer user) {
        SharedPreferences sp = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_GENDER, user.getGender());
        editor.putString(KEY_PROFESSION, user.getProfession());
        editor.putString(KEY_PHONE, user.getPhoneNumber());

        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sp = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String token = sp.getString(KEY_TOKEN, null);
        return token != null && !token.isEmpty();
    }

    public Customer getUser() {
        SharedPreferences sp = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        Customer user = new Customer();
        user.setId(sp.getInt(KEY_ID, -1));
        user.setUsername(sp.getString(KEY_USERNAME, null));
        user.setEmail(sp.getString(KEY_EMAIL, null));
        user.setToken(sp.getString(KEY_TOKEN, null));
        user.setRole(sp.getString(KEY_ROLE, null));
        user.setGender(sp.getString(KEY_GENDER, null));
        user.setProfession(sp.getString(KEY_PROFESSION, null));
        user.setPhoneNumber(sp.getString(KEY_PHONE, null));

        return user;
    }

    public String getToken() {
        SharedPreferences sp = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_TOKEN, null);
    }

    public void logout() {
        SharedPreferences sp = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}
