package xyz.jienan.refreshed.headlines;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.source_select.SourcesSelectActivity;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.network.NewsSourcesBean;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.news_list.NewsListFragment;

/**
 * Created by jienanzhang on 17/07/2017.
 */

public class HeadlinesFragment extends Fragment implements HeadlinesContract.View {

    private Adapter adapter;
    private TabLayout tabLayout;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ViewPager viewPager;
    private HeadlinesContract.Presenter mPresenter;
    private StatefulLayout stateful;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPresenter = new HeadlinesPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        realm = Realm.getDefaultInstance();
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
        mPresenter.loadSources();
        stateful.showLoading();
        sharedPreferences = getActivity().getSharedPreferences("refreshed_source", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return stateful;
    }


    private void setupViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        adapter = new Adapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (adapter != null) {
                    int position = tab.getPosition();
                    INewsListFragmentListener fragment = (INewsListFragmentListener) adapter.getItem(position);
                    fragment.scrollTo(0);
                }
            }
        });
    }

    private void addSourcesToAdapter(List<NewsSourceBean> sourceList) {
//        adapter.removeAllFragment();
        for (NewsSourceBean source : sourceList) {
            NewsListFragment fragment = NewsListFragment.newInstance(source.getId(), source.getName());
            Log.d("zjn", "sources added here " + source);
            adapter.addFragment(fragment, source.getName());
        }
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public void renderSources(List<NewsSourceBean> sourceList) {
        if (sourceList != null && sourceList.size() > 0) {
            stateful.showContent();
            tabLayout.setVisibility(View.VISIBLE);
            filterBySourcesSelection(sourceList);
        } else {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadSources();
                    stateful.showLoading();
                }
            });
        }

    }

    private class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        private FragmentManager fm;

        public Adapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public void removeAllFragment() {
            mFragments.clear();
            mFragmentTitles.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            INewsListFragmentListener fragment = (INewsListFragmentListener) object;
            if (fragment != null) {
                fragment.update();
            }
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refreshed, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sources: {
                Intent intent = new Intent(getActivity(), SourcesSelectActivity.class);
                startActivityForResult(intent, 1);
            }
        }

        return true;
    }

    private void filterBySourcesSelection(List<NewsSourceBean> sources) {
        RealmQuery<NewsSourceBean> query = realm.where(NewsSourceBean.class);
        if (query.count() == 0) {
            List<NewsSourceBean> sourceList = sources.subList(0, 4); // TODO change to top 4 sources
            addSourcesToAdapter(sourceList);
        } else {
            RealmResults<NewsSourceBean> results = query.greaterThan("index", -1).findAll().sort("index");
            addSourcesToAdapter(results.subList(0, results.size()));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            RealmQuery<NewsSourceBean> query = realm.where(NewsSourceBean.class);
            RealmResults<NewsSourceBean> results = query.greaterThan("index", -1).findAll().sort("index");
            addSourcesToAdapter(results.subList(0, results.size()));
            getFragmentManager()
                    .beginTransaction()
                    .detach(HeadlinesFragment.this)
                    .attach(HeadlinesFragment.this)
                    .commit();

        }
    }
}
