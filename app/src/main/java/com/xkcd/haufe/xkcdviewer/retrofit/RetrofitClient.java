package com.xkcd.haufe.xkcdviewer.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit instance;
    public static Retrofit getInstance(){

        if (instance == null)
            instance = new Retrofit.Builder().baseUrl("https://www.xkcd.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
        return instance;
    }

    public static IXkcdAPI getAPI() {

        return RetrofitClient.getInstance().create(IXkcdAPI.class);

    }
}
