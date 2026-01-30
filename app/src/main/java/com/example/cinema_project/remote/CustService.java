package com.example.cinema_project.remote;

import com.example.cinema_project.model.Customer;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CustService {
    @FormUrlEncoded
    @POST("users/login")
    Call<Customer> login(@Field("username") String username,
                         @Field("password") String password);

    @FormUrlEncoded
    @POST("users/login")
    Call<Customer> loginEmail(@Field("email") String email,
                          @Field("password") String password);

    @FormUrlEncoded
    @POST("users")
    Call<Customer> signUp(
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("profession") String profession,
            @Field("phone") String phone,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("users/{id}")
    Call<Customer> updateUser(
            @Header("api-key") String apiKey,
            @Path("id") int userId,
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("profession") String profession,
            @Field("phone") String phone
    );

}
