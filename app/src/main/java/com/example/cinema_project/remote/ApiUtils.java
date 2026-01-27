package com.example.cinema_project.remote;

public class ApiUtils {
    // REST API server URL
    public static final String BASE_URL = "https://aptitude.my/cinema/api/";
    public static final String UPLOADS_URL = "https://aptitude.my/cinema/api/";
    // return UserService instance
    public static CustService getCustService() {
        return RetrofitClient.getClient(BASE_URL).create(CustService.class);
    }

    public static MovieService getMovieService() {
        return RetrofitClient.getClient(BASE_URL).create(MovieService.class);
    }

    public static BookingService getBookingService() {
        return RetrofitClient.getClient(BASE_URL).create(BookingService.class);
    }

}
