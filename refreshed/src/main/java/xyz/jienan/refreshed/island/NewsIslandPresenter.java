package xyz.jienan.refreshed.island;

import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;

/**
 * Created by Jienan on 2018/1/25.
 */

public class NewsIslandPresenter implements NewsIslandContract.Presenter {

    private NewsIslandContract.View mView;
    private IDBManager dbManager;


    NewsIslandPresenter(NewsIslandContract.View view) {
        mView = view;
        dbManager = new RealmManager();
    }

    @Override
    public boolean ifTopicsExist(String topics) {
        return dbManager.getTopicsRequest(topics) != null;
    }

    @Override
    public boolean addTopics(String topics) {
        return dbManager.addTopics(topics);
    }
}
