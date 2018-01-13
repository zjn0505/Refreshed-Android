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
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.gturedi.views.StatefulLayout;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.jienan.refreshed.NetworkUtils;
import xyz.jienan.refreshed.NewsQueryTask;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.SourcesSelectActivity;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.news_list.NewsListFragment;

import static android.view.View.GONE;
import static xyz.jienan.refreshed.NetworkUtils.NEWS_API_SOURCES_URL;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPresenter = new HeadlinesPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
//        querySources();
        mPresenter.loadSources();
        sharedPreferences = getActivity().getSharedPreferences("refreshed_source", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        return stateful;
    }

    private void querySources() {
        URL url = NetworkUtils.buildUrl(NEWS_API_SOURCES_URL);
        new NewsQueryTask(new SourceListener(), NewsSourceBean.class).execute(url);
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

    private void addSourcesToAdapter(Set<String> sourceList) {
//        adapter.removeAllFragment();
        for (String source : sourceList) {
            String[] sourcesSplit = source.split("\\|");
            NewsListFragment fragment = NewsListFragment.newInstance(sourcesSplit[0], sourcesSplit[1]);
            Log.d("zjn", "sources added here " + source);
            adapter.addFragment(fragment, sourcesSplit[1]);
        }
        adapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public void renderSources(NewsSourceBean sources) {
        if (sources != null) {
            if (sources.getSources() != null) {
                stateful.showContent();
                tabLayout.setVisibility(View.VISIBLE);
                filterBySourcesSelection(sources);
            }
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

    private class SourceListener implements NewsQueryTask.IAsyncTaskListener {

        @Override
        public void onPreExecute() {
            tabLayout.setVisibility(View.GONE);
        }

        @Override
        public void onPostExecute(Serializable result) {

            Gson gson = new Gson();
            String json = gson.toJson(result);
            editor.putString("sources", json);
            editor.apply();
            if (result instanceof NewsSourceBean) {

                NewsSourceBean bean = (NewsSourceBean) result;

                filterBySourcesSelection(bean);
            }
        }
    }

    private void filterBySourcesSelection(NewsSourceBean bean) {
        Set<String> selectedSet = sharedPreferences.getStringSet("selected_sources", null);
        if (selectedSet == null) {
            List<NewsSourceBean.SourcesBean> sourceList = bean.getSources().subList(0, 4);
            Set<String> selectedSources = new HashSet();
            for (NewsSourceBean.SourcesBean source : sourceList) {
                String id = source.getId();
                String name = source.getName();
                selectedSources.add(id + "|" + name);
            }
            editor.putStringSet("selected_sources", selectedSources);
            editor.commit();
            addSourcesToAdapter(selectedSources);
        } else {
            addSourcesToAdapter(selectedSet);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Set<String> selectedSet = sharedPreferences.getStringSet("selected_sources", null);
            if (selectedSet != null) {
                addSourcesToAdapter(selectedSet);
                getFragmentManager()
                        .beginTransaction()
                        .detach(HeadlinesFragment.this)
                        .attach(HeadlinesFragment.this)
                        .commit();
            }
        }
    }
}
