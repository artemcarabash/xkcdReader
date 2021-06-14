package com.xkcd.haufe.xkcdviewer.ui.comicsfragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.xkcd.haufe.xkcdviewer.R;
import com.xkcd.haufe.xkcdviewer.databinding.ComicsFragmentBinding;
import com.xkcd.haufe.xkcdviewer.model.Comic;
import com.xkcd.haufe.xkcdviewer.retrofit.IXkcdAPI;
import com.xkcd.haufe.xkcdviewer.utils.Common;
import com.xkcd.haufe.xkcdviewer.utils.ZoomOutPageTransformer;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ComicsViewPagerFragment extends Fragment {

    private int comicNumber;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IXkcdAPI iXkcdAPI;
    private ComicsFragmentBinding binding;

    private ViewPager2 mPager;
    private ComicsViewPagerAdapter mPagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ComicsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);

        mPagerAdapter = new ComicsViewPagerAdapter(getActivity(), 5);
        mPager = binding.pager;
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(new ZoomOutPageTransformer());

        iXkcdAPI = Common.getAPI();
        fetchComic();
        Log.d("Comic Number", String.valueOf(comicNumber));

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void fetchComic() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setMessage("Please Wait...").setCancelable(false).show();

        compositeDisposable.add(iXkcdAPI.getLatestComic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comic) throws Exception {
                                   comicNumber = comic.getNumber();
                                   mPagerAdapter.updateMaxComicNumber(comic.getNumber());
                                   mPager.setCurrentItem(mPagerAdapter.getItemCount());
                                   dialog.dismiss();
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getActivity(), "Error loading comics", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }));
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.random) {
            Random random = new Random();
            mPager.setCurrentItem(random.nextInt(comicNumber + 1));
            return true;
        } else if (id == R.id.current) {
            mPager.setCurrentItem(comicNumber);
        }
        return super.onOptionsItemSelected(item);
    }


}
