package com.example.friendnavi;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceAPI {

    @FormUrlEncoded
    @POST("join.php")
    Call<String> userJoin(@Field("id") String id,
                          @Field("nickname") String nickname,
                          @Field("password") String password,
                          @Field("passwordRe") String passwordRe,
                          @Field("phone") String phone);

    @FormUrlEncoded
    @POST("checkid.php")
    Call<String> checkID(@Field("id") String id);

    @FormUrlEncoded
    @POST("checknick.php")
    Call<String> checkNickname(@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("login.php")
    Call<String> userLogin(@Field("id") String id,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("search.php")
    Call<String> getSearchResult(@Field("query") String query);

    @GET("routes.php")
    Call<TrafficData> getRoutes(@Query("start") String start,
                           @Query("goal") String goal,
                           @Query("option") String option);

    @FormUrlEncoded
    @POST("kakaoJoin.php")
    Call<String> kakaoJoin(@Field("id") String id,
                           @Field("nickname") String nickname);

    @GET("addRoom.php")
    Call<String> addRoom(@Query("nickname") String nickname);

    @GET("chatting.php")
    Call<String> saveChatting(@Query("roomNo") String roomNo,
                              @Query("time") String time,
                              @Query("sendUser") String sendUser,
                              @Query("content") String content);

}