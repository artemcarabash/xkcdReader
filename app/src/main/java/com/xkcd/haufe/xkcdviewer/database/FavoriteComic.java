package com.xkcd.haufe.xkcdviewer.database;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.xkcd.haufe.xkcdviewer.model.Comic;

@Entity(tableName = "favorite_comics")
public class FavoriteComic {
    public static DiffUtil.ItemCallback<FavoriteComic> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteComic>() {
        @Override
        public boolean areItemsTheSame(@NonNull FavoriteComic oldItem, @NonNull FavoriteComic newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavoriteComic oldItem, @NonNull FavoriteComic newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == getClass()) {
            return true;
        }

        FavoriteComic favoriteComic = (FavoriteComic) obj;
        return favoriteComic.num.equals(this.num);
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "month")
    private String month;

    @ColumnInfo(name = "number")
    private String num;

    @ColumnInfo(name = "link")
    private String link;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo(name = "transcript")
    private String transcript;

    @ColumnInfo(name = "alt")
    private String alt;

    @ColumnInfo(name = "img")
    private String img;

    @ColumnInfo(name = "title")
    private String title;

    public FavoriteComic(String month, String num, String link, String year,
                    String transcript, String alt, String img, String title) {
        this.month = month;
        this.num = num;
        this.link = link;
        this.year = year;
        this.transcript = transcript;
        this.alt = alt;
        this.img = img;
        this.title = title;
    }
    public FavoriteComic(Comic comic){
        this.month = comic.getMonth();
        this.num = comic.getNumber().toString();
        this.link = comic.getLink();
        this.year = comic.getYear();
        this.transcript = comic.getTranscript();
        this.alt = comic.getSubTitle();
        this.img = comic.getImageUrl();
        this.title = comic.getTitle();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public String getNum() {
        return num;
    }

    public String getLink() {
        return link;
    }

    public String getYear() {
        return year;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getAlt() {
        return alt;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }
}
