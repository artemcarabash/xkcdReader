package com.xkcd.haufe.xkcdviewer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.xkcd.haufe.xkcdviewer.databinding.ActivityMainBinding;

import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import model.Comic;
import retrofit.IXkcdAPI;
import utils.Common;

public class MainActivity extends AppCompatActivity {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IXkcdAPI iXkcdAPI;
    private ViewPager2 mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private int newestComicNumber = 0;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        iXkcdAPI = Common.getAPI();
        fetchComic();

        mPagerAdapter = new ScreenSlidePagerAdapter(this, newestComicNumber);
        mPager = binding.pager;
        mPager.setOffscreenPageLimit(1);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageTransformer(new ZoomOutPageTransformer());

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
        } else if (id == R.id.current) {
            mPager.setCurrentItem(newestComicNumber);
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
                               public void accept(Comic comic) throws Exception {
                                   newestComicNumber = comic.getNumber();
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

}
