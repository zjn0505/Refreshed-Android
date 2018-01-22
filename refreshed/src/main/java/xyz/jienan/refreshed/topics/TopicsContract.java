package xyz.jienan.refreshed.topics;

import java.util.List;

import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;
import xyz.jienan.refreshed.network.entity.TopicsSearchBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class TopicsContract {
    interface View {
        void renderTopics(List<NewsTopicsRequest> sources);
        void renderTopicsSearch(List<TopicsSearchBean> topics);
    }
    interface Presenter {
        void loadTopics(boolean withCandidates);
        void searchTopics(String query);
    }
}
