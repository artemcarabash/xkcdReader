package com.xkcd.haufe.xkcdviewer.repository;

import android.content.Context;
import android.os.AsyncTask;
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
import com.xkcd.haufe.xkcdviewer.utils.AppExecutors;
import com.xkcd.haufe.xkcdviewer.utils.Common;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FavoriteComicRepository {
    private static final String TAG = FavoriteComicRepository.class.getSimpleName();
    private static volatile FavoriteComicRepository INSTANCE;

    private final FavoriteComicDao favComicsDao;
    private final AppExecutors executors;
    private IXkcdAPI xkcdAPI;


    private FavoriteComicRepository(Context context, AppExecutors executors) {
        ComicsDatabase db = ComicsDatabase.getInstance(context);
        this.favComicsDao = db.favComicsDao();
        this.executors = executors;
        xkcdAPI = Common.getAPI();
    }

    public static FavoriteComicRepository getInstance(Context context,
                                                      AppExecutors executors) {
        if (INSTANCE == null) {
            // If there is no instance available, create a new one
            synchronized (FavoriteComicRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavoriteComicRepository(context, executors);
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

    public LiveData<Comic> getLiveDataLoader(int num) {
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

                            }
                        }));
        return data;
    }

    /*
    Get an item by Id from the database
     */
    public void addOrRemoveFromDb(String comicNum, ResultFromCallback callback) {
        new getItemByNum(comicNum, favComicsDao, callback).execute();
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
                                   Integer list = comic.getNumber();
                                   data.postValue(list);
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

    /*
    Query the item on a background thread via AsyncTask
     */
    private static class getItemByNum extends AsyncTask<Void, Void, Boolean> {

        private String comicNum;
        private FavoriteComicDao favComicsDao;
        private ResultFromCallback callback;

        public getItemByNum(String comicNum, FavoriteComicDao favComicsDao,
                            ResultFromCallback resultFromCallback) {
            this.comicNum = comicNum;
            this.favComicsDao = favComicsDao;
            this.callback = resultFromCallback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean isFav = comicNum.equals(favComicsDao.getComicByNum(comicNum));
            Log.d(TAG, "doInBackground: Item is in the db: " + isFav);

            return isFav;
        }

        @Override
        protected void onPostExecute(Boolean isFav) {
            callback.setResult(isFav);
        }
    }

}
