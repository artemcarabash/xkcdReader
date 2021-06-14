package com.xkcd.haufe.xkcdviewer.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.xkcd.haufe.xkcdviewer.repository.FavoriteComicRepository;
import com.xkcd.haufe.xkcdviewer.ui.favoritesfragment.FavoriteViewModel;

public class FavoriteViewModelFactory implements ViewModelProvider.Factory {
    private final FavoriteComicRepository repository;

    public FavoriteViewModelFactory(FavoriteComicRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FavoriteViewModel.class)) {
            return (T) new FavoriteViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
