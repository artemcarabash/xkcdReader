package com.xkcd.haufe.xkcdviewer.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xkcd.haufe.xkcdviewer.R;
import com.xkcd.haufe.xkcdviewer.databinding.ActivityMainBinding;
import com.xkcd.haufe.xkcdviewer.ui.comicsfragment.ComicsViewPagerFragment;
import com.xkcd.haufe.xkcdviewer.ui.favoritesfragment.FavoritesFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentTabId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, new ComicsViewPagerFragment());
        ft.commit();

        BottomNavigationView navigationView = binding.navigation;
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();

            if (currentTabId != id) {
                displaySelectedScreen(id);
            }

            return true;
        }
    };

    private void displaySelectedScreen(int id) {
        Fragment fragment = null;
        if (id == R.id.browse) {
            currentTabId = id;
            fragment = new ComicsViewPagerFragment();
        } else if (id == R.id.favorites) {
            currentTabId = id;
            fragment = new FavoritesFragment();
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }
    }

}
