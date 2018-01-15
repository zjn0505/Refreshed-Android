package xyz.jienan.refreshed.source_select;

import java.util.List;

import xyz.jienan.refreshed.network.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class SourceSelectContract {
    interface View {
        void renderSources(List<NewsSourceBean> sources);
        void renderSourcesWithReorder(List<NewsSourceBean> sources, int from, int to);
    }
    interface Presenter {
        void loadSources();
        void changeSelection(List<NewsSourceBean> sourceList, boolean wasSelected, int position);
    }
}
