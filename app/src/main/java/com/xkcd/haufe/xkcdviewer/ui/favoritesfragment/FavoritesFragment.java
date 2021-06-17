package com.xkcd.haufe.xkcdviewer.ui.favoritesfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xkcd.haufe.xkcdviewer.R;
import com.xkcd.haufe.xkcdviewer.database.FavoriteComic;
import com.xkcd.haufe.xkcdviewer.databinding.FragmentFavoritesBinding;
import com.xkcd.haufe.xkcdviewer.viewmodel.FavoriteViewModel;


public class FavoritesFragment extends Fragment {
    private static final String TAG = FavoritesFragment.class.getSimpleName();

    private FavoriteViewModel favoriteViewModel;
    private FavoriteComicsAdapter favoriteAdapter;
    private PagedList<FavoriteComic> favoriteComicPagedList;
    private FragmentFavoritesBinding binding;
    private RecyclerView recyclerView;
    private TextView errorTV;

    public FavoritesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerviewFavs;
        errorTV = binding.errorMessage;

        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        favoriteAdapter = new FavoriteComicsAdapter();

        getFavoriteComics();

        recyclerView.setAdapter(favoriteAdapter);

        return view;
    }

    private void getFavoriteComics() {
        favoriteViewModel.getFavComics().observe(getViewLifecycleOwner(), new Observer<PagedList<FavoriteComic>>() {
            @Override
            public void onChanged(@Nullable PagedList<FavoriteComic> favoriteComics) {
                if (favoriteComics != null && favoriteComics.size() > 0) {
                    Log.d(TAG, "Submit comics to the Adapter " + favoriteComics.size());
                    favoriteAdapter.submitList(favoriteComics);
                    errorTV.setVisibility(View.GONE);
                } else {
                    errorTV.setVisibility(View.VISIBLE);
                }

                favoriteComicPagedList = favoriteComics;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.favorite_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                deleteItemsWithConfirmation();
                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteItemsWithConfirmation() {

        if (favoriteComicPagedList.size() > 0) {

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage(R.string.are_you_sure);
            builder.setTitle(R.string.delete_all_title);

            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    favoriteViewModel.deleteAllComics();
                    refreshFavoritesList();
                }
            });

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void refreshFavoritesList() {

        favoriteViewModel.refreshFavComics()
                .observe(this, new Observer<PagedList<FavoriteComic>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<FavoriteComic> favComics) {
                        if (favComics != null) {
                            favoriteAdapter.submitList(favComics);
                            favoriteComicPagedList = favComics;
                        }
                    }
                });
    }
}
