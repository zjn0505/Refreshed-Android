package xyz.jienan.refreshed.headlines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.news_list.NewsListFragment;
import xyz.jienan.refreshed.source_select.SourcesSelectActivity;

/**
 * Created by jienanzhang on 17/07/2017.
 */

public class HeadlinesFragment extends Fragment implements HeadlinesContract.View {

    private Adapter adapter;
    private TabLayout tabLayout;
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
        mPresenter.loadSources();
        stateful.showLoading();
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
        adapter.updateSource(sourceList);
        viewPager.setCurrentItem(0);

    }

    @Override
    public void renderSources(List<NewsSourceBean> sourceList) {
        if (sourceList != null && sourceList.size() > 0) {
            stateful.showContent();
            tabLayout.setVisibility(View.VISIBLE);
            addSourcesToAdapter(sourceList);
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

    private class Adapter extends FragmentStatePagerAdapter {
        private List<NewsSourceBean> sourceList = new ArrayList<>();
        private FragmentManager fm;

        public Adapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public int getItemPosition(Object object) {
            INewsListFragmentListener fragment = (INewsListFragmentListener) object;
            String title = fragment.getFragmentName();

            for (NewsSourceBean source : sourceList) {
                if (source.getName().equals(title)) {
                    return sourceList.indexOf(source);
                }
            }
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            NewsSourceBean source = sourceList.get(position);
            NewsListFragment fragment = NewsListFragment.newInstance(source.getId(), source.getName());
            return fragment;
        }

        @Override
        public int getCount() {
            return sourceList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            NewsSourceBean source = sourceList.get(position);
            return source.getName();
        }

        public void updateSource(List<NewsSourceBean> sourceList) {
            this.sourceList = sourceList;
            notifyDataSetChanged();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            mPresenter.loadSources();
//            getFragmentManager()
//                    .beginTransaction()
//                    .detach(HeadlinesFragment.this)
//                    .attach(HeadlinesFragment.this)
//                    .commit();

        }
    }
}
