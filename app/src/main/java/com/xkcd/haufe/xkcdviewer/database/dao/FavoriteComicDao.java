package com.xkcd.haufe.xkcdviewer.database.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;

import java.util.List;

import io.reactivex.Single;


@Dao
public interface FavoriteComicDao {
    @Insert
    void insertComic(FavoriteComic favComic);

    @Query("SELECT * FROM favorite_comics")
    DataSource.Factory<Integer, FavoriteComic> getAllFavComics();

    @Query("SELECT number FROM favorite_comics WHERE number = :comicNum")
    String getComicByNum(String comicNum);

    @Query("SELECT EXISTS(SELECT * FROM favorite_comics WHERE number = :comicNum)")
    Single<Boolean> isComicInDB(String comicNum);

    @Query("DELETE FROM favorite_comics WHERE number = :comicNumber")
    void deleteComic(String comicNumber);

    @Query("DELETE FROM favorite_comics")
    void deleteAllData();
}
