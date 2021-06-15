package com.xkcd.haufe.xkcdviewer.ui.favoritesfragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedList;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.xkcd.haufe.xkcdviewer.R;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.databinding.SingleFavItemBinding;

import java.util.List;

public class FavoriteComicsAdapter extends PagedListAdapter<FavoriteComic, RecyclerView.ViewHolder> {

    private static final String TAG = FavoriteComicsAdapter.class.getSimpleName();
    private List<FavoriteComic> favComicList;

    public FavoriteComicsAdapter() {
        super(FavoriteComic.DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        com.xkcd.haufe.xkcdviewer.databinding.SingleFavItemBinding binding = SingleFavItemBinding.inflate(layoutInflater, parent, false);

        return new FavComicsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItem(position) != null) {
            ((FavComicsViewHolder) viewHolder).bindTo(favComicList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (favComicList != null) {
            Log.d(TAG, "Size of the fav list: " + favComicList.size());
            return favComicList.size();
        }

        return super.getItemCount();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<FavoriteComic> currentList) {
        // Set the fav list with the current list
        favComicList = currentList;

        notifyDataSetChanged();

        super.onCurrentListChanged(currentList);
    }

    public class FavComicsViewHolder extends RecyclerView.ViewHolder {

        ImageView favImage;
        TextView favTitle;
        TextView favID;

        public FavComicsViewHolder(@NonNull SingleFavItemBinding binding) {
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
                Picasso.get()
                        .load(imageString)
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(favImage);
            }
        }
    }

}
