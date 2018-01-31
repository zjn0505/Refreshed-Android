package xyz.jienan.refreshed.news_list;


import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.ArticlesBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

import static xyz.jienan.refreshed.network.NetworkService.USE_CACHE;

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
    public void loadList(String newsSource, int type, String bypassCache) {
        NetworkService.NewsAPI newsAPI = NetworkService.getNewsAPI();
        if (type == R.integer.type_source) {
            Observable<ArticlesBean> articlesObservable = newsAPI.getHeadLinesBySource(newsSource, bypassCache);
            articlesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArticlesBean>() {
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
            Observable<ArticlesBean> alterObsearvable = newsAPI.getCustomQuery("", "en", "2018-01-01", bypassCache);
            NewsTopicsRequest topicsRequest = dbManger.getTopicsRequest(newsSource);
            String query = "", category = "";
            if (topicsRequest != null) {
                if (!TextUtils.isEmpty(topicsRequest.getCategory())) {
                    category = topicsRequest.getCategory();
                } else {
                    query = topicsRequest.getQ();
                    alterObsearvable = newsAPI.getCustomQuery(query, topicsRequest.getLanguage(), "2018-01-01", bypassCache);
                }
            } else {
                topicsRequest = new NewsTopicsRequest();
                query = newsSource;
                alterObsearvable = newsAPI.getCustomQuery(query, topicsRequest.getLanguage(), "2018-01-01", bypassCache);
            }

            Observable<ArticlesBean> articlesObservable = newsAPI.getTopics(query, category, bypassCache);
            final Observable<ArticlesBean> finalAlterObservable = alterObsearvable;
            articlesObservable.subscribeOn(Schedulers.io()).flatMap(new Function<ArticlesBean, ObservableSource<ArticlesBean>>() {
                @Override
                public ObservableSource<ArticlesBean> apply(ArticlesBean articlesBean) throws Exception {
                    if (articlesBean.getTotalResults() == 0 || articlesBean.getArticles().size() == 0) {
                        return finalAlterObservable;
                    } else {
                        return Observable.just(articlesBean);
                    }
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ArticlesBean>() {
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
