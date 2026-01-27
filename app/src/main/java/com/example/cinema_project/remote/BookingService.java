package com.example.cinema_project.remote;

import com.example.cinema_project.model.Booking;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BookingService {

    @GET("Bookings")
    Call<List<Booking>> getAllBooking(@Header("api-key") String api_key);

    @GET("Bookings/{BookingID}")
    Call<Booking> getBooking(@Header("api-key") String api_key, @Path("BookingID") int id);

    @FormUrlEncoded
    @POST("Bookings")
    Call<ResponseBody> addBooking(@Header ("api-key") String apiKey, @Field("BookingDate") String date,
                                  @Field("BookingTime") String time, @Field("TicketQuantity") int quantity,
                                  @Field("seat") String seat, @Field("PaymentType") String type,
                                  @Field("TotalPrice") double total, @Field("user_id") int custid,
                                  @Field("movie_code") int code);
}
