package com.xkcd.haufe.xkcdviewer;

import android.os.AsyncTask;
import android.util.Log;

import androidx.paging.DataSource;

import com.xkcd.haufe.xkcdviewer.callbacks.ResultFromCallback;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.database.dao.FavoriteComicDao;

public class FavoriteComicRepository {
    private static final String TAG = FavoriteComicRepository.class.getSimpleName();
    private static volatile FavoriteComicRepository INSTANCE;

    private final FavoriteComicDao favComicsDao;
    private final AppExecutors executors;


    private FavoriteComicRepository(FavoriteComicDao comicsDao, AppExecutors executors) {
        this.favComicsDao = comicsDao;
        this.executors = executors;
    }

    public static FavoriteComicRepository getInstance(FavoriteComicDao comicsDao,
                                                  AppExecutors executors) {
        if (INSTANCE == null) {
            // If there is no instance available, create a new one
            synchronized (FavoriteComicRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FavoriteComicRepository(comicsDao, executors);
                }
            }
        }

        return INSTANCE;
    }

    public DataSource.Factory<Integer, FavoriteComic> getAllFavs() {
        return favComicsDao.getAllFavComics();
    }

    /**
     * Method for inserting a new item in the database
     *
     * @param favComic the object being saved in the db
     */
    public void insertItem(final FavoriteComic favComic) {
        executors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favComicsDao.insertComic(favComic);
            }
        });
    }

    /**
     * Method for deleting an item by its number
     *
     * @param comicNumber the number of the comic being deleted
     */
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

    /*
    Get an item by Id from the database
     */
    public void addOrRemoveFromDb(String comicNum, ResultFromCallback callback) {
        new getItemByNum(comicNum, favComicsDao, callback).execute();
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
