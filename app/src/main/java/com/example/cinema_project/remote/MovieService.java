package com.example.cinema_project.remote;

import com.example.cinema_project.model.DeleteResponse;
import com.example.cinema_project.model.Movie;
import com.example.cinema_project.model.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MovieService {

    @GET("movie?order=name&orderType=asc")
    Call<List<Movie>> getAllMovie(@Header("api-key") String api_key);

    @GET("movie/{moviecode}")
    Call<Movie> getMovie(@Header("api-key") String api_key, @Path("moviecode") int id);

    @POST("movie")
    Call<Movie> addMovie(@Header ("api-key") String apiKey,
                         @Field("title") String title, @Field("description") String description,
                         @Field("length") int length, @Field("genre") String genre,
                         @Field("userid") int staffID,@Field("banner") String imageBanner,
                        @Field("image") String imagePoster);


    @DELETE("movie/{moviecode}")
    Call<DeleteResponse> deleteMovie(@Header ("api-key") String apiKey, @Path("moviecode") int id);

    @FormUrlEncoded
    @POST("movie/{moviecode}")
    Call<Movie> updateMovie(@Header ("api-key") String apiKey, @Path("moviecode") int id,
                            @Field("title") String title, @Field("description") String description,
                            @Field("length") int length, @Field("genre") String genre,
                            @Field("banner") String imageBanner, @Field("image") String imagePoster);
}
