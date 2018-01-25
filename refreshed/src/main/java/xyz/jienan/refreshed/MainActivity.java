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

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.util.List;

import io.reactivex.functions.Consumer;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;
import xyz.jienan.refreshed.sources_fragment.headlines.HeadlinesFragment;
import xyz.jienan.refreshed.sources_fragment.topics.TopicsFragment;
import xyz.jienan.refreshed.ui.DrawerAdapter;
import xyz.jienan.refreshed.ui.DrawerAdapter.IDrawerItemClickListener;
import xyz.jienan.refreshed.ui.GlideFaceDetector;

import static xyz.jienan.refreshed.MetaUtils.ADMOB_APP_ID;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private RecyclerView rvHeadlines;
    private RecyclerView rvTopics;
    private DrawerAdapter headlinesAdapter;
    private DrawerAdapter topicsAdapter;
    private FragmentManager fragmentManager;
    private DrawerMainItemClickListener drawerItemClickListener;
    private DrawerSubItemClickListener drawerSubItemClickListener;

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
        drawerSubItemClickListener = new DrawerSubItemClickListener();
        headlinesAdapter = new DrawerAdapter(drawerSubItemClickListener, DrawerAdapter.TYPE_HEADLINES);
        topicsAdapter = new DrawerAdapter(drawerSubItemClickListener, DrawerAdapter.TYPE_TOPICS);
        rvHeadlines.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvTopics.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvHeadlines.setHasFixedSize(true);
        rvTopics.setHasFixedSize(true);
        rvHeadlines.setAdapter(headlinesAdapter);
        rvTopics.setAdapter(topicsAdapter);

        fragmentManager = getSupportFragmentManager();
        drawerItemClickListener = new DrawerMainItemClickListener();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        if (headlinesFragment == null) {
            headlinesFragment = new HeadlinesFragment();
        }
        if (topicsFragment == null) {
            topicsFragment = new TopicsFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, topicsFragment,"topics");
        transaction.add(R.id.container, headlinesFragment,"headlines");
        transaction.hide(topicsFragment);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        transaction.commit();
        setTitle("Headlines");
        ((RefreshedApplication) getApplication()).bus().toObservable()
                .subscribe(new Consumer<Object>() {

                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof List) {
                            if(((List)o).size()>0){
                                if (((List)o).get(0) instanceof ITabEntity) {
                                    updateDrawer((List<ITabEntity>) o);
                                }
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
        TextView tvHeadline = findViewById(R.id.tv_headlines);
        tvHeadline.setOnClickListener(drawerItemClickListener);

        TextView tvTopics = findViewById(R.id.tv_topics);
        tvTopics.setOnClickListener(drawerItemClickListener);
    }


    private HeadlinesFragment headlinesFragment;
    private TopicsFragment topicsFragment;

    private class DrawerMainItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_headlines || v.getId() == R.id.tv_topics) {
                switchFragment(v.getId());
            }
        }
    }

    private class DrawerSubItemClickListener implements IDrawerItemClickListener {

        @Override
        public void onDrawerItemClicked(int type, String sourceName) {
            if (type == DrawerAdapter.TYPE_HEADLINES) {
                switchFragment(R.id.tv_headlines);
                headlinesFragment.switchToSource(sourceName);
            } else if (type == DrawerAdapter.TYPE_TOPICS) {
                switchFragment(R.id.tv_topics);
                topicsFragment.switchToSource(sourceName);
            }
        }
    }

    private void switchFragment(int id) {
        Fragment toShow = null;
        Fragment toHide = null;
        String title = "";
        if (id == R.id.tv_headlines) {
            toShow = fragmentManager.findFragmentByTag("headlines");
            toHide = fragmentManager.findFragmentByTag("topics");
            title = "Headlines";
        } else if (id == R.id.tv_topics) {
            toShow = fragmentManager.findFragmentByTag("topics");
            toHide = fragmentManager.findFragmentByTag("headlines");
            title = "Topics";
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(toHide).show(toShow).commit();
        mDrawerLayout.closeDrawers();
        setTitle(title);
    }

    void updateDrawer(List<ITabEntity> items) {
        if (items.size() > 10) {
            items = items.subList(0, 10);
        }
        if (items.get(0) instanceof NewsSourceBean) {
            headlinesAdapter.updateList(items);
        } else if (items.get(0) instanceof NewsTopicsRequest) {
            topicsAdapter.updateList(items);
        }
    }
}
