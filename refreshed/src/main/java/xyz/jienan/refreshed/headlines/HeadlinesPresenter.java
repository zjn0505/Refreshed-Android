package xyz.jienan.refreshed.headlines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.network.NewsSourcesBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class HeadlinesPresenter implements HeadlinesContract.Presenter {


    private HeadlinesContract.View mView;
    private IDBManager dbManger;

    public HeadlinesPresenter(HeadlinesContract.View view) {
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
                        mView.renderSources(dbManger.reorderByIndex(sourceList));
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
