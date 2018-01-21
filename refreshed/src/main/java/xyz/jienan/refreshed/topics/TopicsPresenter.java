package xyz.jienan.refreshed.topics;

import java.util.List;

import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

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
//        Observable<NewsSourcesBean> sourcesObservable = NetworkService.getNewsAPI().getSources("", "");
//        sourcesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsSourcesBean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(NewsSourcesBean sources) {
//                if (sources != null) {
//                    List<NewsSourceBean> sourceList = sources.getSources();
//                    if (sourceList != null && sourceList.size() > 0) {
//                        sourceList = dbManger.reorderByIndex(sourceList);
//                        int i = 0;
//                        for (NewsSourceBean bean : sourceList) {
//                            if (bean.getIndex() < 0) {
//                                break;
//                            }
//                            i++;
//                        }
//
//                        mView.renderTopics(sourceList.subList(0, i));
//                    }
//                } else {
//                    mView.renderTopics(null);
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                mView.renderTopics(null);
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });

    }
}
