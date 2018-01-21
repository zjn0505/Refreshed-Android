package xyz.jienan.refreshed.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.jienan.refreshed.network.bean.NewsSourceBean;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.news_list.NewsListFragment;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class NewsPagerAdapter extends FragmentStatePagerAdapter {
    private List<NewsSourceBean> sourceList = new ArrayList<>();
    private FragmentManager fm;

    public NewsPagerAdapter(FragmentManager fm) {
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