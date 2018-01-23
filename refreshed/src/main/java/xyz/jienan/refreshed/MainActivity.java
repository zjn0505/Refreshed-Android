/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.jienan.refreshed;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

import io.reactivex.functions.Consumer;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.headlines.HeadlinesFragment;
import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.topics.TopicsFragment;
import xyz.jienan.refreshed.ui.DrawerAdapter;
import xyz.jienan.refreshed.ui.GlideFaceDetector;

import static xyz.jienan.refreshed.MetaUtils.ADMOB_APP_ID;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private RecyclerView rvHeadlines;
    private RecyclerView rvTopics;
    private DrawerAdapter headlinesAdapter;
    private DrawerAdapter topicsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, MetaUtils.getMeta(ADMOB_APP_ID));
        GlideFaceDetector.initialize(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        rvHeadlines = findViewById(R.id.rv_headlines);
        rvTopics = findViewById(R.id.rv_topics);
        headlinesAdapter = new DrawerAdapter();
        topicsAdapter = new DrawerAdapter();
        rvHeadlines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTopics.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvHeadlines.setHasFixedSize(true);
        rvTopics.setHasFixedSize(true);
        rvHeadlines.setAdapter(headlinesAdapter);
        rvTopics.setAdapter(topicsAdapter);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new HeadlinesFragment()).commit();

        ((RefreshedApplication) getApplication()).bus().toObservable()
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof List) {
                            if(((List)o).size()>0 && (((List)o).get(0) instanceof NewsSourceBean)){
                                updateDrawer(R.id.nav_headlines, (List<ITabEntity>) o);
                            }
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        switch (AppCompatDelegate.getDefaultNightMode()) {
//            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
//                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_AUTO:
//                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_YES:
//                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_NO:
//                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
//                break;
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        GlideFaceDetector.releaseDetector();
        super.onDestroy();
    }

    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//                selectDrawerItem(menuItem);
//
//                return true;
//            }
//        });

        TextView tvHeadline = findViewById(R.id.tv_headlines);
        tvHeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new HeadlinesFragment()).commit();
                mDrawerLayout.closeDrawers();
                setTitle("Headlines");
            }
        });

        TextView tvTopics = findViewById(R.id.tv_topics);
        tvTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, new TopicsFragment()).commit();
                mDrawerLayout.closeDrawers();
                setTitle("Topics");
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_headlines:
                fragmentClass = HeadlinesFragment.class;
                break;
            case R.id.nav_topics:
                fragmentClass = TopicsFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment == null) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();
        setTitle(menuItem.getTitle());
    }

    void updateDrawer(int menuGroupId, List<ITabEntity> items) {
        if (menuGroupId == R.id.nav_headlines) {
            headlinesAdapter.updateList(items);
        }


    }

}
