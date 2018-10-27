package xyz.jienan.refreshed.news_list;


import android.text.TextUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import xyz.jienan.refreshed.MetaUtils;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.TimeUtils;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.ArticlesBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

import static xyz.jienan.refreshed.MetaUtils.ALTER_HOST_API_KEY;
import static xyz.jienan.refreshed.network.NetworkService.REQ_UPDATE_TOPICS;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NewsListPresenter implements NewsListContract.Presenter {

    private NewsListContract.View mView;

    private IDBManager dbManger;

    NewsListPresenter(NewsListContract.View view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadList(final String newsSource, int type, int newsDays, String bypassCache) {
        final NetworkService.NewsAPI newsAPI = NetworkService.getNewsAPI();
        if (type == R.integer.type_source) {
            newsAPI.getHeadLinesBySource(newsSource, bypassCache)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(articlesBean -> mView.renderList(articlesBean),
                            e -> {
                                Timber.e(e);
                                mView.renderList(null);
                            });
        } else if (type == R.integer.type_topic) {
            Observable<ArticlesBean> alterObservable = newsAPI.getCustomQuery("", "en", "2018-01-01", bypassCache);
            NewsTopicsRequest topicsRequest = dbManger.getTopicsRequest(newsSource);
            String query = "", category = "";
            if (topicsRequest != null) {
                if (!TextUtils.isEmpty(topicsRequest.getCategory())) {
                    category = topicsRequest.getCategory();
                } else {
                    query = topicsRequest.getQ();
                    alterObservable = newsAPI.getCustomQuery(query, topicsRequest.getLanguage(),
                            TimeUtils.getNewsAgedFrom(topicsRequest.getNewsAgeInDays()), bypassCache);
                }
            } else {
                topicsRequest = new NewsTopicsRequest();
                query = newsSource;
                alterObservable = newsAPI.getCustomQuery(query, topicsRequest.getLanguage(), TimeUtils.getNewsAgedFrom(newsDays), bypassCache);
                dbManger.adjustTopicsDays(newsSource, newsDays);
            }
            final Observable<ArticlesBean> finalAlterObservable = alterObservable;
            Observable<ArticlesBean> articlesObservable =
                    topicsRequest.isForceEverything() ? finalAlterObservable : newsAPI.getTopics(query, category, bypassCache);
            articlesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .filter(articlesBean -> {
                        final NewsTopicsRequest finalTopicsRequest = dbManger.getTopicsRequest(newsSource);
                        int articlesCount = articlesBean.getArticles().size();
                        if (articlesCount == 0) {
                            if (dbManger.getTopicsRequest(newsSource) != null)
                                dbManger.setForceEverything(newsSource);
                        } else {
                            if (finalTopicsRequest.isForceEverything()) {
                                int totalArticles = articlesBean.getTotalResults();
                                final int newsDays1 = finalTopicsRequest.getNewsAgeInDays();
                                if (dbManger.getTopicsRequest(newsSource) != null)
                                    if (totalArticles > 800 && newsDays1 > 3) {
                                        dbManger.adjustTopicsDays(newsSource, newsDays1 - 10 > 0 ? (newsDays1 - 10) : (newsDays1 - 3));
                                    } else if (totalArticles < 20) {
                                        dbManger.adjustTopicsDays(newsSource, newsDays1 +10);
                                    } else {
                                        newsAPI.updateTopicNewsDays(REQ_UPDATE_TOPICS, newsSource, newsDays1, MetaUtils.getMeta(ALTER_HOST_API_KEY))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(ignored -> {},
                                                        Timber::e);
                                    }
                            }
                        }
                        return true;
                    }).observeOn(Schedulers.io())
                    .flatMap(articlesBean -> {
                        int articlesCount = articlesBean.getArticles().size();
                        if (articlesCount <= 5) {
                            return finalAlterObservable;
                        } else {
                            return Observable.just(articlesBean);
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(articlesBean -> mView.renderList(articlesBean), e -> {
                        mView.renderList(null);
                        Timber.e(e);
                    });
        }
    }
}
