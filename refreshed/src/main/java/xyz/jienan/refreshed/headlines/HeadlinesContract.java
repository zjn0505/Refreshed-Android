package xyz.jienan.refreshed.headlines;

import xyz.jienan.refreshed.network.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class HeadlinesContract {
    interface View {
        void renderSources(NewsSourceBean sources);
    }
    interface Presenter {
        void loadSources();
    }
}
