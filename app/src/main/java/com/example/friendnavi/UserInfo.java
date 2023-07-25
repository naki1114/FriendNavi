package com.example.friendnavi;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserInfo {

//    @FormUrlEncoded
//    @POST("login.php")
//    Call<LoginResponse> userLogin(@Body LoginData data);

    @FormUrlEncoded
    @POST("join.php")
    Call<String> userJoin(@Field("id") String id,
                          @Field("nickname") String nickname,
                          @Field("password") String password,
                          @Field("passwordRe") String passwordRe,
                          @Field("phone") String phone,
                          @Field("addHome") String addHome,
                          @Field("addComp") String addComp);

}