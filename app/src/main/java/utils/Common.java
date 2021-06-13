package utils;

import retrofit.IXkcdAPI;
import retrofit.RetrofitClient;

public class Common {

    public static IXkcdAPI getAPI() {

        return RetrofitClient.getInstance().create(IXkcdAPI.class);

    }

}
