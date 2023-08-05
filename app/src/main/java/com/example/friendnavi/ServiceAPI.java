package com.example.friendnavi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

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

}