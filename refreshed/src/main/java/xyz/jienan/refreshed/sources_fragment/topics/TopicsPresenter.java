package xyz.jienan.refreshed.sources_fragment.topics;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;
import xyz.jienan.refreshed.network.entity.TopicsSearchBean;

import static xyz.jienan.refreshed.network.NetworkService.HOST_TOPICS_SEARCH;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class TopicsPresenter implements TopicsContract.Presenter {


    private TopicsContract.View mView;
    private IDBManager dbManger;

    public TopicsPresenter(TopicsContract.View view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadTopics(boolean withCandidates) {
        List<NewsTopicsRequest> topicsList = dbManger.getTopics(withCandidates);
        mView.renderTopics(topicsList);
    }

    @Override
    public void searchTopics(String query) {
        Observable<List<TopicsSearchBean>> searchObservalbe = NetworkService.getNewsAPI().getTopicsSuggestions(HOST_TOPICS_SEARCH, query);
        searchObservalbe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<TopicsSearchBean>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<TopicsSearchBean> topicsSearchBeans) {
                mView.renderTopicsSearch(topicsSearchBeans);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
