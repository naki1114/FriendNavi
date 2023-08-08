package com.example.friendnavi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchAPI {
    @GET("search/local.json")
    Call<String> getSearchResult(
            @Header("X-Naver-Client-Id") String id,
            @Header("X-Naver-Client-Secret") String pw,
            @Query("query") String query
    );
}
