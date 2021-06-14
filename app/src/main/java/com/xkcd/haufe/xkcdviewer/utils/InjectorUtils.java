package com.xkcd.haufe.xkcdviewer.utils;

import android.content.Context;

import com.xkcd.haufe.xkcdviewer.AppExecutors;
import com.xkcd.haufe.xkcdviewer.FavoriteComicRepository;
import com.xkcd.haufe.xkcdviewer.database.ComicsDatabase;
import com.xkcd.haufe.xkcdviewer.viewmodel.FavoriteViewModelFactory;

public class InjectorUtils {

    private static FavoriteComicRepository provideRepository(Context context) {
        ComicsDatabase database = ComicsDatabase.getInstance(context);
        AppExecutors executors = AppExecutors.getInstance();

        return FavoriteComicRepository.getInstance(database.favComicsDao(), executors);
    }

    public static FavoriteViewModelFactory provideFavComicViewModelFactory(Context context) {
        FavoriteComicRepository repository = provideRepository(context.getApplicationContext());
        return new FavoriteViewModelFactory(repository);
    }
}
