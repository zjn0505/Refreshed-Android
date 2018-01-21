package xyz.jienan.refreshed.topics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.bean.NewsSourceBean;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class TopicsFragment extends Fragment implements TopicContract.View {

    private StatefulLayout stateful;
    private NewsPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TopicContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = new TopicPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        mPresenter.loadSources();
        stateful.showLoading();
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
//                    mPresenter.loadSources();
                    stateful.showLoading();
                }
            });
        }

    }
}
