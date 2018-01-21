package xyz.jienan.refreshed.topics;

import java.util.List;

import xyz.jienan.refreshed.network.bean.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class TopicContract {
    interface View {
        void renderSources(List<NewsSourceBean> sources);
    }
    interface Presenter {
        void loadSources();
    }
}
