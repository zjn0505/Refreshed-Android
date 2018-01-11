package xyz.jienan.refreshed.headlines;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.NewsSourceBean;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class HeadlinesPresenter implements HeadlinesContract.Presenter {


    private HeadlinesContract.View mView;

    public HeadlinesPresenter(HeadlinesContract.View view) {
        mView = view;
    }

    @Override
    public void loadSources() {
        Observable<NewsSourceBean> sourcesObservable = NetworkService.getNewsAPI().getSources("", "");
        sourcesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsSourceBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(NewsSourceBean sources) {
                mView.renderSources(sources);
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
