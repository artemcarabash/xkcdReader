package com.xkcd.haufe.xkcdviewer.database.dao;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.model.Comic;

import java.util.List;



@Dao
public interface FavoriteComicDao {
    @Insert
    void insertComic(FavoriteComic favComic);

    @Query("SELECT * FROM favorite_comics")
    List<FavoriteComic> allComics();

    @Query("SELECT * FROM favorite_comics")
    DataSource.Factory<Integer, FavoriteComic> getAllFavComics();

    @Query("SELECT number FROM favorite_comics WHERE number = :comicNum")
    String getComicByNum(String comicNum);

    @Query("DELETE FROM favorite_comics WHERE number = :comicNumber")
    void deleteComic(String comicNumber);

    @Query("DELETE FROM favorite_comics")
    void deleteAllData();
}
