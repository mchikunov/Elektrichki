package com.me.elektrichki;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Api {

    String BASE_URL = "https://api.rasp.yandex.net/v3.0/";

    @GET("search")
    Call<String> getNews(
            @QueryMap Map<String, String> options
    );
}


