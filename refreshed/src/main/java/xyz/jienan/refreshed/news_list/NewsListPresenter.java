package xyz.jienan.refreshed.news_list;


import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.entity.ArticlesBean;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;
    private IDBManager dbManger;


    public NewsListPresenter(NewsListContract.View  view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadList(String newsSource, int type, boolean bypassCache) {
        NetworkService.NewsAPI newsAPI = NetworkService.getNewsAPI();
        if (type == R.integer.type_source) {
            Observable<ArticlesBean> headlineObservable = bypassCache ? newsAPI.getHeadLinesBySourceWithoutCache(newsSource)
                    : newsAPI.getHeadLinesBySource(newsSource);
            headlineObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArticlesBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ArticlesBean articlesBean) {
                    mView.renderList(articlesBean);
                }

                @Override
                public void onError(Throwable e) {
                    mView.renderList(null);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            });
        } else if (type == R.integer.type_topic) {

            NewsTopicsRequest topicsRequest = dbManger.getTopicsRequest(newsSource);
            String query = "", category = "";
            if (!TextUtils.isEmpty(topicsRequest.getCategory())) {
                category = topicsRequest.getCategory();
            } else {
                query = topicsRequest.getQ();
            }

            Observable<ArticlesBean> headlineObservable = bypassCache ? newsAPI.getTopicsWithoutCache(query, category)
                    : newsAPI.getTopics(query, category);
            headlineObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArticlesBean>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(ArticlesBean articlesBean) {
                    mView.renderList(articlesBean);
                }

                @Override
                public void onError(Throwable e) {
                    mView.renderList(null);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }
}
