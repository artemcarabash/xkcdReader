package com.xkcd.haufe.xkcdviewer;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    protected int mMaxComicNumber;

    public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity, int mMaxComicNumber) {
        super(fragmentActivity);
        this.mMaxComicNumber = mMaxComicNumber;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        position++;
        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
        screenSlidePageFragment.comicNumber = position;
        return screenSlidePageFragment;
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
