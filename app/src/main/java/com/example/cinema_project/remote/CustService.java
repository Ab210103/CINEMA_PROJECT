package com.example.cinema_project.remote;

import com.example.cinema_project.model.Customer;
import com.example.cinema_project.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
public interface CustService {
    @FormUrlEncoded
    @POST("users/login")
    Call<Customer> login(@Field("username") String name,
                         @Field("password") String password);

    @FormUrlEncoded
    @POST("users/login")
    Call<Customer> loginEmail(@Field("email") String email,
                          @Field("password") String password);

    @FormUrlEncoded
    @POST("users/register")
    Call<RegisterResponse> signUp(
            @Field("username") String name,
            @Field("email") String email,
            @Field("phonenumber") String phonenumber,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("profession") String profession
    );

}
