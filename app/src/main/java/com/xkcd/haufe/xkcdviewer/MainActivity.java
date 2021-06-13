package com.xkcd.haufe.xkcdviewer;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Comic;
import retrofit.IXkcdAPI;
import utils.Common;

public class MainActivity extends FragmentActivity  {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IXkcdAPI iXkcdAPI;
    private ViewPager2 mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private int newestComicNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);

        iXkcdAPI = Common.getAPI();

        mPagerAdapter = new ScreenSlidePagerAdapter(this, newestComicNumber);
        mPager = findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(2);
        mPager.setPageTransformer(new ZoomOutPageTransformer());

        fetchComic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.random) {
            Random random = new Random();
            mPager.setCurrentItem(random.nextInt(newestComicNumber + 1));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchComic() {

        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setMessage("Please Wait...").setCancelable(false).show();

        compositeDisposable.add(iXkcdAPI.getLatestComic()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Comic>() {
                               @Override
                               public void accept(Comic comics) throws Exception {
                                   Toast.makeText(MainActivity.this, comics.getImageUrl(), Toast.LENGTH_LONG).show();
                                   newestComicNumber = comics.getNumber();
                                   mPagerAdapter.updateMaxComicNumber(newestComicNumber);
                                   mPager.setCurrentItem(mPagerAdapter.getItemCount());
                                   dialog.dismiss();
                               }
                           }
                        , new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(MainActivity.this, "Error loading comics", Toast.LENGTH_SHORT).show();
                            }
                        }));
    }


    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        protected int mMaxComicNumber;

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity, int mMaxComicNumber) {
            super(fragmentActivity);
            this.mMaxComicNumber = mMaxComicNumber;
        }


//        @Override
//        public Fragment getItem(int position) {
//            // Arrays are 0 based, but the comic is 1 based, so always add 1.
//            position++;
//
//            ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
//            screenSlidePageFragment.comicNumber = position;
//            return screenSlidePageFragment;
//        }
//
//
//        @Override
//        public int getCount() {
//            if (mMaxComicNumber <= 0) {
//                return 1;
//            }
//            return mMaxComicNumber;
//        }

        public void updateMaxComicNumber(int mMaxComicNumber) {
            Log.d("Adapter", "Got New Max Comic Number Updating Adapter " + mMaxComicNumber);
            this.mMaxComicNumber = mMaxComicNumber;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Arrays are 0 based, but the comic is 1 based, so always add 1.
            position++;

            ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
            screenSlidePageFragment.comicNumber = position;
            return screenSlidePageFragment;
        }

        @Override
        public int getItemCount() {
            if (mMaxComicNumber <= 0) {
                return 1;
            }
            return mMaxComicNumber;
        }
    }
}
