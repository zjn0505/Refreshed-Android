package xyz.jienan.refreshed.island;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;

import static xyz.jienan.refreshed.network.NetworkService.REQ_TOPICS_DAYS;

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

    @Override
    public void checkNewsDays(String source) {
        NetworkService.NewsAPI newsAPI = NetworkService.getNewsAPI();
        newsAPI.getTopicNewsDays(REQ_TOPICS_DAYS, source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(ignored -> 10)
                .onErrorResumeNext(Observable.just(30))
                .doOnNext(mView::onNewsDaysReady)
                .subscribe();
    }
}
