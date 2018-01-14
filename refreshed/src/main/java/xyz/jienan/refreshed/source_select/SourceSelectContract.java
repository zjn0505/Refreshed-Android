package xyz.jienan.refreshed.source_select;

import java.util.List;

import xyz.jienan.refreshed.network.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class SourceSelectContract {
    interface View {
        void renderSources(List<NewsSourceBean> sources);
    }
    interface Presenter {
        void loadSources();
        void changeSelection(String sourceId, boolean wasSelected, int position);
    }
}
