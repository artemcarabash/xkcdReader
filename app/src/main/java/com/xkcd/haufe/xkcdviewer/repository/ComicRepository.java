package com.xkcd.haufe.xkcdviewer.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.ComicsDatabase;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.database.dao.FavoriteComicDao;
import com.xkcd.haufe.xkcdviewer.model.Comic;
import com.xkcd.haufe.xkcdviewer.retrofit.IXkcdAPI;
import com.xkcd.haufe.xkcdviewer.retrofit.RetrofitClient;
import com.xkcd.haufe.xkcdviewer.utils.AppExecutors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ComicRepository {
    private static final String TAG = ComicRepository.class.getSimpleName();
    private static volatile ComicRepository INSTANCE;

    private final FavoriteComicDao favComicsDao;
    private final AppExecutors executors;
    private IXkcdAPI xkcdAPI;


    private ComicRepository(Context context, AppExecutors executors) {
        ComicsDatabase db = ComicsDatabase.getInstance(context);
        this.favComicsDao = db.favComicsDao();
        this.executors = executors;
        xkcdAPI = RetrofitClient.getAPI();
    }

    public static ComicRepository getInstance(Context context,
                                              AppExecutors executors) {
        if (INSTANCE == null) {
            // If there is no instance available, create a new one
            synchronized (ComicRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ComicRepository(context, executors);
                }
            }
        }

        return INSTANCE;
    }

    public DataSource.Factory<Integer, FavoriteComic> getAllFavorites() {
        return favComicsDao.getAllFavComics();
    }

    //Method for inserting a new item in the database
    public void insertItem(final FavoriteComic favComic) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favComicsDao.insertComic(favComic);
            }
        });
    }

    // Method for deleting an item by its number
    public void deleteItem(final String comicNumber) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Item is deleted from the db!");
                favComicsDao.deleteComic(comicNumber);
            }
        });
    }

    // Delete all list of favorite comics
    // Create a warning dialog for the user before allowing them to delete all data
    public void deleteAllItems() {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favComicsDao.deleteAllData();
            }
        });
    }

    public LiveData<Comic> getLiveDataLoader(final int num) {
        Log.d(TAG, "getLiveDataLoader: " + num);
        final MutableLiveData<Comic> data = new MutableLiveData<>();
        new CompositeDisposable().add(xkcdAPI.getComic(num)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comic) throws Exception {
                                   data.postValue(comic);
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                data.postValue(null);
                            }
                        }));
        return data;
    }

    /*
    Get an item by Id from the database
     */
    public void addOrRemoveFromDb(String comicNum, final ResultFromCallback callback) {

        new CompositeDisposable().add(favComicsDao.isComicInDB(comicNum)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean exists) throws Exception {
                                   callback.setResult(exists);
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                callback.setResult(false);
                            }
                        }));
    }

    public LiveData<Integer> getBrowseData() {
        Log.d(TAG, "getBrowseData: ");

        final MutableLiveData<Integer> data = new MutableLiveData<>();

        new CompositeDisposable().add(xkcdAPI.getLatestComic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comic) throws Exception {
                                   Integer number = comic.getNumber();
                                   data.postValue(number);
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                data.postValue(0);
                            }
                        }));

        return data;
    }

}
