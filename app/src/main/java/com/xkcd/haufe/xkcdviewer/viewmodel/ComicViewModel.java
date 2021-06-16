package com.xkcd.haufe.xkcdviewer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.xkcd.haufe.xkcdviewer.model.Comic;
import com.xkcd.haufe.xkcdviewer.repository.FavoriteComicRepository;
import com.xkcd.haufe.xkcdviewer.utils.AppExecutors;

public class ComicViewModel extends AndroidViewModel {
    AppExecutors executors = AppExecutors.getInstance();
    private FavoriteComicRepository repository;

    public ComicViewModel(@NonNull Application application) {
        super(application);
        repository = FavoriteComicRepository.getInstance(application, executors);
    }

    public LiveData<Integer> getBrowseData() {
        return repository.getBrowseData();
    }

    public LiveData<Comic> getComicByNumber(int num) {
        return repository.getLiveDataLoader(num);
    }

}
