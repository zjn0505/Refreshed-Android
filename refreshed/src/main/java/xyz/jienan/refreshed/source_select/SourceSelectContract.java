package xyz.jienan.refreshed.source_select;

import java.util.List;

import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class SourceSelectContract {
    interface View {
        void renderSources(List<? extends ITabEntity> sources);
        void renderSourcesWithReorder(List<? extends ITabEntity> sources, int from, int to);
    }
    interface Presenter {
        void loadSources();
        void loadTopics();
        void changeSelection(List<? extends ITabEntity> sourceList, boolean wasSelected, int position);
        void reorderSelected(List<? extends ITabEntity> sourceList, int from, int to);
    }
}
