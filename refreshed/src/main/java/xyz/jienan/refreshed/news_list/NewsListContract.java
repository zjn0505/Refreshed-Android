package xyz.jienan.refreshed.news_list;

import xyz.jienan.refreshed.network.HeadlinesBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NewsListContract {
    interface View {
        void renderList(HeadlinesBean headlinesBean);
    }

    interface Presenter {
        void loadList(String newsSource, boolean bypassCache);
    }
}
