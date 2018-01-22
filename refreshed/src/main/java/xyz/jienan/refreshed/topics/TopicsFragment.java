package xyz.jienan.refreshed.topics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.source_select.SourcesSelectActivity;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class TopicsFragment extends Fragment implements TopicsContract.View {

    private StatefulLayout stateful;
    private NewsPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TopicsContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = new TopicsPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
        mPresenter.loadTopics(false);
//        stateful.showLoading(); // TODO don't know why
        return stateful;
    }

    private void setupViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        adapter = new NewsPagerAdapter(getChildFragmentManager());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_topics, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {

            }
        }

        return true;
    }



    private void addTopicsToAdapter(List<NewsTopicsRequest> sourceList) {
        adapter.updateSource(sourceList);
        viewPager.setCurrentItem(0);
    }

    @Override
    public void renderTopics(List<NewsTopicsRequest> sourceList) {
        if (sourceList != null && sourceList.size() > 0) {
            stateful.showContent();
            tabLayout.setVisibility(View.VISIBLE);
            addTopicsToAdapter(sourceList);
        } else {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadTopics(false);
                    stateful.showLoading();
                }
            });
        }

    }
}
