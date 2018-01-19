package xyz.jienan.refreshed.source_select;

import android.content.res.TypedArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.internal.schedulers.NewThreadWorker;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.IconRequest;
import xyz.jienan.refreshed.network.IconsBean;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.network.NewsSourcesBean;

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
    public void changeSelection(List<NewsSourceBean> sourceList, final boolean wasSelected, final int position) {
        int toPosition = dbManger.updateIndex(sourceList, wasSelected, position);
        mView.renderSourcesWithReorder(dbManger.reorderByIndex(sourceList), position, toPosition);
    }


}
