package com.xkcd.haufe.xkcdviewer.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private static final Object LOCK = new Object();
    private static AppExecutors INSTANCE;
    private final Executor mDiskIO;

    private AppExecutors(Executor diskIO) {
        mDiskIO = diskIO;
    }

    public static AppExecutors getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                INSTANCE = new AppExecutors(Executors.newSingleThreadExecutor());
            }
        }
        return INSTANCE;
    }

    public Executor diskIO() {
        return mDiskIO;
    }

}
