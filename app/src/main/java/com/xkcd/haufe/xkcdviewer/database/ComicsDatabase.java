package com.xkcd.haufe.xkcdviewer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.xkcd.haufe.xkcdviewer.database.dao.FavoriteComicDao;

@Database(
        entities = {FavoriteComic.class},
        version = 1,
        exportSchema = false)
public abstract class ComicsDatabase extends RoomDatabase {
    private static ComicsDatabase INSTANCE;
    private static final Object LOCK = new Object();
    private static final String XKCD_DB = "comics.db";

    // Reference the DAO from the database class
    public abstract FavoriteComicDao favComicsDao();

    public static ComicsDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                INSTANCE = create(context);
            }
        }
        return INSTANCE;
    }

    private static ComicsDatabase create(Context context) {
        RoomDatabase.Builder<ComicsDatabase> databaseBuilder = Room.databaseBuilder(
                context.getApplicationContext(), ComicsDatabase.class, XKCD_DB);

        return (databaseBuilder.build());
    }
}
