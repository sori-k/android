package com.example.ex09;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RemoteService {
    public static final String BASE_URL = "http://192.168.0.189:8080";

    @POST("/user/login")
    Call<Integer> login(@Body UserVO vo);

    @GET("/user/list")
    Call<List<UserVO>> list(
            @Query("page") int page,
            @Query("size") int size,
            @Query("word") String word);

    @GET("/user/read/{uid}")
    Call<UserVO> read(@Path("uid") String uid);
}