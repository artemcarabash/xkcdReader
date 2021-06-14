package com.xkcd.haufe.xkcdviewer.ui.favoritesfragment;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.xkcd.haufe.xkcdviewer.FavoriteComicRepository;
import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;

public class FavoriteViewModel extends ViewModel {
    private FavoriteComicRepository repository;
    private LiveData<PagedList<FavoriteComic>> favComicsList;

    public FavoriteViewModel(FavoriteComicRepository repository) {
        this.repository = repository;

        init();
    }

    private void init() {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(10)
                .build();

        favComicsList = new LivePagedListBuilder<>(repository
                .getAllFavs(),
                pagedListConfig).build();
    }

    public LiveData<PagedList<FavoriteComic>> getFavComics() {
        return favComicsList;
    }

    public void deleteAllComics() {
        repository.deleteAllItems();
    }

    public LiveData<PagedList<FavoriteComic>> refreshFavComics(Application application) {
        PagedList.Config pagedListConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(10)
                .setPageSize(10)
                .build();

        favComicsList = new LivePagedListBuilder<>(repository
                .getAllFavs(),
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
