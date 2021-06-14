package com.xkcd.haufe.xkcdviewer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ComicsViewPagerAdapter extends FragmentStateAdapter {
    protected int mMaxComicNumber;

    public ComicsViewPagerAdapter(FragmentActivity fragmentActivity, int mMaxComicNumber) {
        super(fragmentActivity);
        this.mMaxComicNumber = mMaxComicNumber;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        position++;
        ComicsViewPagerFragment comicsViewPagerFragment = new ComicsViewPagerFragment();
        comicsViewPagerFragment.comicNumber = position;
        return comicsViewPagerFragment;
    }

    @Override
    public int getItemCount() {
        return mMaxComicNumber;
    }

    public void updateMaxComicNumber(int mMaxComicNumber) {
        Log.d("Adapter", "Got New Max Comic Number Updating Adapter " + mMaxComicNumber);
        this.mMaxComicNumber = mMaxComicNumber;
        notifyDataSetChanged();
    }
}
