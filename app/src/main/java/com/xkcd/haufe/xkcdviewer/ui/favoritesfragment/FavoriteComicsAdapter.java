package com.xkcd.haufe.xkcdviewer.ui.favoritesfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.databinding.SingleFavoriteItemBinding;
import com.xkcd.haufe.xkcdviewer.utils.PicassoImageLoadingService;

import java.util.List;

public class FavoriteComicsAdapter extends PagedListAdapter<FavoriteComic, RecyclerView.ViewHolder> {

    private static final String TAG = FavoriteComicsAdapter.class.getSimpleName();
    private List<FavoriteComic> favoriteComicList;

    public FavoriteComicsAdapter() {
        super(FavoriteComic.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SingleFavoriteItemBinding binding = SingleFavoriteItemBinding.inflate(layoutInflater, parent, false);

        return new FavoriteComicsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItem(position) != null) {
            ((FavoriteComicsViewHolder) viewHolder).bindTo(favoriteComicList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (favoriteComicList != null) {
            Log.d(TAG, "Size of the fav list: " + favoriteComicList.size());
            return favoriteComicList.size();
        }

        return super.getItemCount();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<FavoriteComic> currentList) {
        // Set the fav list with the current list
        favoriteComicList = currentList;

        notifyDataSetChanged();

        super.onCurrentListChanged(currentList);
    }

    public class FavoriteComicsViewHolder extends RecyclerView.ViewHolder {

        ImageView favImage;
        TextView favTitle;
        TextView favID;

        public FavoriteComicsViewHolder(@NonNull SingleFavoriteItemBinding binding) {
            super(binding.getRoot());
            favImage = binding.favImage;
            favTitle = binding.favTitle;
            favID = binding.favId;
        }

        public void bindTo(FavoriteComic favComic) {
            if (favComic != null) {
                String imageString = favComic.getImg();
                String titleString = favComic.getTitle();
                String idString = favComic.getNum();

                favTitle.setText(titleString);
                favID.setText("#" + idString);
                new PicassoImageLoadingService().loadImage(imageString, favImage);
            }
        }
    }

}
