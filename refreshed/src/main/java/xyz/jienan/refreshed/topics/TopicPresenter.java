package xyz.jienan.refreshed.topics;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.bean.NewsSourceBean;
import xyz.jienan.refreshed.network.bean.NewsSourcesBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class TopicPresenter implements TopicContract.Presenter {


    private TopicContract.View mView;
    private IDBManager dbManger;

    public TopicPresenter(TopicContract.View view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadSources() {
        Observable<NewsSourcesBean> sourcesObservable = NetworkService.getNewsAPI().getSources("", "");
        sourcesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsSourcesBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NewsSourcesBean sources) {
                if (sources != null) {
                    List<NewsSourceBean> sourceList = sources.getSources();
                    if (sourceList != null && sourceList.size() > 0) {
                        sourceList = dbManger.reorderByIndex(sourceList);
                        int i = 0;
                        for (NewsSourceBean bean : sourceList) {
                            if (bean.getIndex() < 0) {
                                break;
                            }
                            i++;
                        }

                        mView.renderSources(sourceList.subList(0, i));
                    }
                } else {
                    mView.renderSources(null);
                }

            }

            @Override
            public void onError(Throwable e) {
                mView.renderSources(null);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

    }
}
