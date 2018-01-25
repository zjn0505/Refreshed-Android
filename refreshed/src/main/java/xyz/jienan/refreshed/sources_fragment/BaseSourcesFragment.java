package xyz.jienan.refreshed.sources_fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.gturedi.views.StatefulLayout;

import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

/**
 * Created by Jienan on 2018/1/25.
 */

public class BaseSourcesFragment extends Fragment {

    protected StatefulLayout stateful;
    protected NewsPagerAdapter adapter;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected int landingPage = 0;

    public void switchToSource(String sourceName) {
        landingPage = adapter.getItemPosition(sourceName);
        viewPager.setCurrentItem(landingPage);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setupViewPager(viewPager);
        } else {
            tabLayout.removeOnTabSelectedListener(tabSelectListener);
        }
    }

    protected void setupViewPager(ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(tabSelectListener);
        if (landingPage != 0 && landingPage < adapter.getCount()) {
            viewPager.setCurrentItem(landingPage);
            landingPage = 0;
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    private TabLayout.OnTabSelectedListener tabSelectListener = new TabLayout.OnTabSelectedListener() {
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
                INewsListFragmentListener fragment = adapter.getFragment(position);
                fragment.scrollTo(0);
            }
        }
    };
}
