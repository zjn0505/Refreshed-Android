package xyz.jienan.refreshed.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.news_list.INewsListFragmentListener;
import xyz.jienan.refreshed.news_list.NewsListFragment;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class NewsPagerAdapter extends FragmentStatePagerAdapter {
    private List<? extends ITabEntity> sourceList = new ArrayList<>();
    private FragmentManager fm;
    private HashMap map = new HashMap();


    public NewsPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    @Override
    public int getItemPosition(Object object) {
        INewsListFragmentListener fragment = (INewsListFragmentListener) object;
        String title = fragment.getFragmentName();
        return getItemPosition(title);
    }

    @Override
    public Fragment getItem(int position) {
        ITabEntity source = sourceList.get(position);
        NewsListFragment fragment = NewsListFragment.newInstance(source.getId(), source.getName(), source.getType());
        map.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        map.remove(position);
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return sourceList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        ITabEntity source = sourceList.get(position);
        return source.getName();
    }

    public NewsListFragment getFragment(int position) {
        return (NewsListFragment) map.get(position);
    }

    public void updateSource(List<? extends ITabEntity> sourceList) {
        this.sourceList = sourceList;
        notifyDataSetChanged();
    }

    public int getItemPosition(String sourceName) {
        for (ITabEntity source : sourceList) {
            if (source.getName().equals(sourceName)) {
                return sourceList.indexOf(source);
            }
        }
        return POSITION_NONE;
    }
}