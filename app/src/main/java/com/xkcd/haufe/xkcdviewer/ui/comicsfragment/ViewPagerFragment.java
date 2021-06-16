package com.xkcd.haufe.xkcdviewer.ui.comicsfragment;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.xkcd.haufe.xkcdviewer.R;
import com.xkcd.haufe.xkcdviewer.databinding.ComicsFragmentBinding;
import com.xkcd.haufe.xkcdviewer.utils.ZoomOutPageTransformer;
import com.xkcd.haufe.xkcdviewer.viewmodel.ComicViewModel;

import java.util.Random;


public class ViewPagerFragment extends Fragment {

    private int comicNumber;
    private ComicsFragmentBinding binding;

    private ViewPager2 mPager;
    private ComicsViewPagerAdapter mPagerAdapter;

    private Observer<Integer> mObserver;
    private ComicViewModel mModel;
    private TextView errTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ComicsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        errTextView = binding.errorTV;
        mPager = binding.pager;
        setHasOptionsMenu(true);

        Log.d("Comic Number", String.valueOf(comicNumber));

        mModel = new ViewModelProvider(this).get(ComicViewModel.class);

        mObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer comicNumber) {
                if (comicNumber == null) {
                    mPager.setVisibility(View.GONE);
                    errTextView.setVisibility(View.VISIBLE);
                } else if (comicNumber == 0) {
                    mPager.setVisibility(View.GONE);
                    errTextView.setVisibility(View.VISIBLE);
                } else {
                    setViewPager(comicNumber);
                    errTextView.setVisibility(View.GONE);
                }

            }
        };
        loadData();
        return view;
    }

    private LiveData<Integer> liveData;

    private void loadData() {
        if (liveData != null) {
            liveData.removeObserver(mObserver);
        }
        liveData = mModel.getBrowseData();
        liveData.observe(getViewLifecycleOwner(), mObserver);
    }

    private void setViewPager(int comicNumber) {
        this.comicNumber = comicNumber;
        mPagerAdapter = new ComicsViewPagerAdapter(getActivity(), comicNumber);
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(new ZoomOutPageTransformer());
        mPagerAdapter.updateMaxComicNumber(comicNumber);
        mPager.setCurrentItem(mPagerAdapter.getItemCount());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
