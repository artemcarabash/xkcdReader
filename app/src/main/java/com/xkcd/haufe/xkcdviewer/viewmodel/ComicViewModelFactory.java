package com.xkcd.haufe.xkcdviewer.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ComicViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public ComicViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ComicViewModel.class)) {
            return (T) new ComicViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
