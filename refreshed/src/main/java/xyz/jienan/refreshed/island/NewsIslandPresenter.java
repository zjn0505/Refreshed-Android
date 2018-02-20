package xyz.jienan.refreshed.island;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        Observable observable = newsAPI.getTopicNewsDays(REQ_TOPICS_DAYS, source);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {
                mView.onNewsDaysReady(10);
            }

            @Override
            public void onError(Throwable e) {
                mView.onNewsDaysReady(30);
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
