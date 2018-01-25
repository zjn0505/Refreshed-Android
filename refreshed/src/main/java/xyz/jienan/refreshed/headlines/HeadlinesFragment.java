package xyz.jienan.refreshed.headlines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.List;

import xyz.jienan.refreshed.MainActivity;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.source_select.SourcesSelectActivity;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jienanzhang on 17/07/2017.
 */

public class HeadlinesFragment extends Fragment implements HeadlinesContract.View, MainActivity.IViewPagerHolder {

    private NewsPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HeadlinesContract.Presenter mPresenter;
    private StatefulLayout stateful;
    private int landingPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPresenter = new HeadlinesPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        adapter = new NewsPagerAdapter(getChildFragmentManager());
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
        mPresenter.loadSources();
        stateful.showLoading();
        return stateful;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setupViewPager(viewPager);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
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
        if (landingPage != 0 && landingPage < adapter.getCount()) {
            viewPager.setCurrentItem(landingPage);
            landingPage = 0;
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    private void addSourcesToAdapter(List<NewsSourceBean> sourceList) {
        adapter.updateSource(sourceList);
        if (landingPage != 0 && landingPage < sourceList.size()) {
            viewPager.setCurrentItem(landingPage);
            landingPage = 0;
        } else {
            viewPager.setCurrentItem(0);
        }
        ((RefreshedApplication) getActivity().getApplication()).bus().send(sourceList);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_headlines, menu);
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mPresenter.loadSources();
        }
    }

    @Override
    public void switchToSource(String sourceName) {
        landingPage = adapter.getItemPosition(sourceName);
        viewPager.setCurrentItem(landingPage);
    }
}
