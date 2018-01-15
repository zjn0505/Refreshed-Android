package xyz.jienan.refreshed.base;

import java.util.List;

import xyz.jienan.refreshed.network.NewsSourceBean;

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
}
