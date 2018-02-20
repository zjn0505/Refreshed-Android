package xyz.jienan.refreshed.news_list;

import xyz.jienan.refreshed.network.entity.ArticlesBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NewsListContract {
    interface View {
        void renderList(ArticlesBean articlesBean);
    }

    interface Presenter {

        /**
         * @param newsSource
         * @param type        type_source or type_topic
         * @param bypassCache
         */
        void loadList(String newsSource, int type, int newsDays, String bypassCache);
    }
}
