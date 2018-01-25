package xyz.jienan.refreshed.sources_fragment.headlines;

import java.util.List;

import xyz.jienan.refreshed.network.entity.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class HeadlinesContract {
    public interface View {
        void renderSources(List<NewsSourceBean> sources);
    }
    interface Presenter {
        void loadSources();
    }
}
