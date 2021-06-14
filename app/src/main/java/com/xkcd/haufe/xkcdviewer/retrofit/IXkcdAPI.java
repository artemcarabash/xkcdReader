package com.xkcd.haufe.xkcdviewer.retrofit;

import io.reactivex.Single;
import com.xkcd.haufe.xkcdviewer.model.Comic;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IXkcdAPI {

    @GET("/{id}/info.0.json")
    Single<Comic> getComic(@Path("id") int id);

    @GET("/info.0.json")
    Single<Comic> getLatestComic();
}
