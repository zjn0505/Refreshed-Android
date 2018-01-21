package xyz.jienan.refreshed.topics;

import java.util.List;

import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class TopicsContract {
    interface View {
        void renderTopics(List<NewsTopicsRequest> sources);
    }
    interface Presenter {
        void loadTopics(boolean withCandidates);
    }
}
