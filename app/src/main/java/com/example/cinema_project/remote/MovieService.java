package com.example.cinema_project.remote;

import com.example.cinema_project.model.DeleteResponse;
import com.example.cinema_project.model.FileInfo;
import com.example.cinema_project.model.Movie;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface MovieService {

    @GET("Movies")
    Call<List<Movie>> getAllMovie();

    @GET("Movies/{moviecode}")
    Call<Movie> getMovie(@Header("api-key") String api_key, @Path("moviecode") int id);

    @FormUrlEncoded
    @POST("Movies")
    Call<Movie> addMovie(@Header ("api-key") String apiKey,
                         @Field("title") String title, @Field("description") String description,
                         @Field("length") int length, @Field("genre") String genre,
                         @Field("user_id") int user_id,@Field("banner") String banner,
                        @Field("image") String image);


    @DELETE("Movies/{moviecode}")
    Call<DeleteResponse> deleteMovie(@Header ("api-key") String apiKey, @Path("moviecode") int moviecode);

    @FormUrlEncoded
    @POST("Movies/{moviecode}")
    Call<Movie> updateMovie(@Header ("api-key") String apiKey, @Path("moviecode") int moviecode,
                            @Field("title") String title, @Field("description") String description,
                            @Field("length") int length, @Field("genre") String genre,
                            @Field("banner") String banner, @Field("image") String image);

    @Multipart
    @POST("files")
    Call<FileInfo> uploadFile(@Header ("api-key") String apiKey, @Part MultipartBody.Part file);
}
