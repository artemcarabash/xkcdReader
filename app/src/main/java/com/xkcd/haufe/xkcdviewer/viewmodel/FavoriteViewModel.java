package com.xkcd.haufe.xkcdviewer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.repository.ComicRepository;
import com.xkcd.haufe.xkcdviewer.utils.AppExecutors;

public class FavoriteViewModel extends AndroidViewModel {
    private ComicRepository repository;
    private LiveData<PagedList<FavoriteComic>> favComicsList;
    AppExecutors executors = AppExecutors.getInstance();

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        this.repository = ComicRepository.getInstance(application, executors);
        init();
    }

    private void init() {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(10)
                .build();

        favComicsList = new LivePagedListBuilder<>(repository
                .getAllFavorites(),
                pagedListConfig).build();
    }

    public LiveData<PagedList<FavoriteComic>> getFavComics() {
        return favComicsList;
    }

    public void deleteAllComics() {
        repository.deleteAllItems();
    }

    public LiveData<PagedList<FavoriteComic>> refreshFavComics() {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(10)
                .build();

        favComicsList = new LivePagedListBuilder<>(repository
                .getAllFavorites(),
                pagedListConfig).build();

        return favComicsList;
    }

    public void isAddedToDb(String comicNum, ResultFromCallback resultFromCallback) {
        repository.addOrRemoveFromDb(comicNum, resultFromCallback);
    }

    public void insertInDb(FavoriteComic favComic) {
        repository.insertItem(favComic);
    }

    public void deleteItem(String comicNum) {
        repository.deleteItem(comicNum);
    }
}
