package xyz.jienan.refreshed.news_list;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.network.HeadlinesBean;
import xyz.jienan.refreshed.network.NetworkService;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;


    public NewsListPresenter(NewsListContract.View  view) {
        mView = view;
    }

    @Override
    public void loadList(String newsSource) {
        Observable<HeadlinesBean> headlineObservable = NetworkService.getNewsAPI().getHeadLinesBySource(newsSource);
        headlineObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<HeadlinesBean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(HeadlinesBean headlinesBean) {
                mView.renderList(headlinesBean);
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
