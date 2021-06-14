package com.xkcd.haufe.xkcdviewer.utils;

import com.xkcd.haufe.xkcdviewer.retrofit.IXkcdAPI;
import com.xkcd.haufe.xkcdviewer.retrofit.RetrofitClient;

public class Common {

    public static IXkcdAPI getAPI() {

        return RetrofitClient.getInstance().create(IXkcdAPI.class);

    }

}
