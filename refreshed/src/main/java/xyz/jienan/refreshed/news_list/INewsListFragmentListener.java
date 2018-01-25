package xyz.jienan.refreshed.news_list;

/**
 * Created by jienanzhang on 13/01/2018.
 */

public interface INewsListFragmentListener {
    String getFragmentName();
    void scrollTo(int position);
}
