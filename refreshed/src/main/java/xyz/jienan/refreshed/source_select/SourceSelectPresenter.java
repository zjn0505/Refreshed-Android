package xyz.jienan.refreshed.source_select;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.IconsBean;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.network.entity.NewsSourcesBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

import static xyz.jienan.refreshed.network.NetworkService.HOST_IMAGE_PROXY;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class SourceSelectPresenter implements SourceSelectContract.Presenter {

    private SourceSelectContract.View mView;
    private IDBManager dbManger;

    public SourceSelectPresenter(SourceSelectContract.View view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadSources() {
        Observable<NewsSourcesBean> sourcesObservable = NetworkService.getNewsAPI().getSources("", "");
        sourcesObservable.flatMap(new Function<NewsSourcesBean, Observable<IconsBean>>() {

            @Override
            public Observable<IconsBean> apply(NewsSourcesBean newsSourcesBean) throws Exception {

                JSONArray param = new JSONArray();
                for (NewsSourceBean source : newsSourcesBean.getSources()) {
                    param.put(source.getName());
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),(param).toString());
                return NetworkService.getNewsAPI().getFeatureImage(HOST_IMAGE_PROXY, body);
            }
        }, new BiFunction<NewsSourcesBean, IconsBean, NewsSourcesBean>() {
            @Override
            public NewsSourcesBean apply(NewsSourcesBean newsSourcesBean, IconsBean iconsBean) throws Exception {
                HashMap<String, String> imgUrlMap = new HashMap<>();
                for (int i = 0; i < iconsBean.getSize(); i++) {
                    IconsBean.DataBean bean = iconsBean.getData().get(i);
                    imgUrlMap.put(bean.getSource(), bean.getImgUrl());
                }

                for (NewsSourceBean source : newsSourcesBean.getSources()) {
                    String sourceName = source.getName();
                    source.setImgUrl(imgUrlMap.get(sourceName));
                }
                return newsSourcesBean;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsSourcesBean>() {
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

    @Override
    public void loadTopics() {
        List<NewsTopicsRequest> topicsList = dbManger.getTopics(true);
        Observable.just(topicsList).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<NewsTopicsRequest>, ObservableSource<List<NewsTopicsRequest>>>() {
            @Override
            public ObservableSource<List<NewsTopicsRequest>> apply(List<NewsTopicsRequest> topicsList) throws Exception {
                topicsList = new RealmManager().createCopy(topicsList);
                return Observable.just(topicsList);
            }
        }).observeOn(Schedulers.io()).flatMap(new Function<List<NewsTopicsRequest>, Observable<IconsBean>>() {
            @Override
            public Observable<IconsBean> apply(List<NewsTopicsRequest> topicsList) throws Exception {
                JSONArray param = new JSONArray();
                for (NewsTopicsRequest topic : topicsList) {
                    param.put(topic.getQ());
                }
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),(param).toString());
                return NetworkService.getNewsAPI().getFeatureImage(HOST_IMAGE_PROXY, body);
            }
        }, new BiFunction<List<NewsTopicsRequest>, IconsBean, List<NewsTopicsRequest>>() {
            @Override
            public List<NewsTopicsRequest> apply(List<NewsTopicsRequest> topicsList, IconsBean iconsBean) throws Exception {
                HashMap<String, String> imgUrlMap = new HashMap<>();
                for (int i = 0; i < iconsBean.getSize(); i++) {
                    IconsBean.DataBean bean = iconsBean.getData().get(i);
                    imgUrlMap.put(bean.getSource(), bean.getImgUrl());
                }

                for (NewsTopicsRequest topic : topicsList) {
                    String topicName = topic.getQ();
                    topic.setImgUrl(imgUrlMap.get(topicName));
                }
                return topicsList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<NewsTopicsRequest>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<NewsTopicsRequest> newsTopicsRequests) {
                if (newsTopicsRequests != null && newsTopicsRequests.size() > 0) {
                    mView.renderSources(newsTopicsRequests);
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

    @Override
    public void changeSelection(List<? extends ITabEntity> sourceList, final boolean wasSelected, final int position) {
        if (checkType(sourceList) == R.integer.type_source) {
            int toPosition = dbManger.updateIndex((List<NewsSourceBean>) sourceList, wasSelected, position);
            mView.renderSourcesWithReorder(dbManger.reorderByIndex((List<NewsSourceBean>) sourceList), position, toPosition);
        } else {
            int toPosition = dbManger.updateIndexForTopics((List<NewsTopicsRequest>) sourceList, wasSelected, position);
            mView.renderSourcesWithReorder(dbManger.reorderByIndexForTopics((List<NewsTopicsRequest>) sourceList), position, toPosition);
        }

    }

    @Override
    public void reorderSelected(List<? extends ITabEntity> sourceList, int from, int to) {
        if (checkType(sourceList) == R.integer.type_source)
            dbManger.updateListForReordering((List<NewsSourceBean>) sourceList);
        else
            dbManger.updateListForReorderingForTopics((List<NewsTopicsRequest>) sourceList);
    }

    private int checkType(List<? extends ITabEntity> sourceList) {
        if (sourceList != null && !sourceList.isEmpty() && sourceList.get(0) instanceof NewsSourceBean) {
            return R.integer.type_source;
        } else {
            return R.integer.type_topic;
        }
    }
}
