package com.example.cinema_project.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = "https://aptitude.my/cinema/api/";

    // return UserService instance
    public static CustService getCustService() {
        return RetrofitClient.getClient(BASE_URL).create(CustService.class);
    }

}
