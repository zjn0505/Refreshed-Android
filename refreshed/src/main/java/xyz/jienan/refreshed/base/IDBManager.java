package xyz.jienan.refreshed.base;

import java.util.List;

import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

/**
 * Created by jienanzhang on 15/01/2018.
 */

public interface IDBManager {

    List<NewsSourceBean> reorderByIndex(List<NewsSourceBean> rawList);

    /**
     * @param sourceList the source list before update
     * @param wasSelected the selection state before item clicked
     * @param position the index of position that was clicked
     * @return the target position that the item will be moved to
     */
    int updateIndex(List<NewsSourceBean> sourceList, boolean wasSelected, int position);

    List<? extends ITabEntity> reorderByIndexForTopics(List<NewsTopicsRequest> sourceList);

    int updateIndexForTopics(List<NewsTopicsRequest> sourceList, boolean wasSelected, int position);

    /**
     * Get the list of saved topics
     * @param withCandidates true if return the list with candidates topics
     * @return
     */
    List<NewsTopicsRequest> getTopics(boolean withCandidates);

    /**
     * Query the db to get related topics info
     * @param newsSource
     * @return
     */
    NewsTopicsRequest getTopicsRequest(String newsSource);

    boolean addTopics(String topics);

    void updateListForReordering(List<NewsSourceBean> sourceList);

    void updateListForReorderingForTopics(List<NewsTopicsRequest> sourceList);
}
