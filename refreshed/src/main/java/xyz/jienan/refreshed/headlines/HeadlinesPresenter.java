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
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.network.NewsSourcesBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class HeadlinesPresenter implements HeadlinesContract.Presenter {


    private HeadlinesContract.View mView;
    private Realm realm;

    public HeadlinesPresenter(HeadlinesContract.View view) {
        mView = view;
        realm = Realm.getDefaultInstance();
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
                        mView.renderSources(applySourcesToRealm(sourceList));
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

    private List<NewsSourceBean> applySourcesToRealm(List<NewsSourceBean> sourceList) {
        RealmResults<NewsSourceBean> result = realm.where(NewsSourceBean.class).greaterThan("index", -1).findAll();
        if (result.size() == 0) {
            int end = sourceList.size() >= 4 ? 4 : sourceList.size();
            return sourceList.subList(0, end);
        }
        List<NewsSourceBean> list = new ArrayList<NewsSourceBean>();
        for (NewsSourceBean source : sourceList) {
            NewsSourceBean sourceDB = result.where().equalTo("id", source.getId()).findFirst();
            if (sourceDB != null)
                list.add(sourceDB);
        }
        Collections.sort(list, new NewsSourceBean.SourceIndexComparator());
        return list;

    }
}
