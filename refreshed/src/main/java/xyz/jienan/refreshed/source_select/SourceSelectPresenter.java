package xyz.jienan.refreshed.source_select;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import timber.log.Timber;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.IconsBean;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.network.entity.NewsSourcesBean;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;

import static xyz.jienan.refreshed.network.NetworkService.REQ_IMAGE_PROXY;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class SourceSelectPresenter implements SourceSelectContract.Presenter {

    private SourceSelectContract.View mView;
    private IDBManager dbManger;

    SourceSelectPresenter(SourceSelectContract.View view) {
        mView = view;
        dbManger = new RealmManager();
    }

    @Override
    public void loadSources() {
        Observable<NewsSourcesBean> sourcesObservable = NetworkService.getNewsAPI().getSources("", "");
        sourcesObservable.flatMap((Function<NewsSourcesBean, Observable<IconsBean>>) newsSourcesBean -> {

            JSONArray param = new JSONArray();
            for (NewsSourceBean source : newsSourcesBean.getSources()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "source");
                jsonObject.put("query", source.getName());
                param.put(jsonObject);
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (param).toString());
            return NetworkService.getNewsAPI().getFeatureImage(REQ_IMAGE_PROXY, body);
        }, (newsSourcesBean, iconsBean) -> {
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
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sources -> {
                    if (sources != null) {
                        List<NewsSourceBean> sourceList = sources.getSources();
                        if (sourceList != null && sourceList.size() > 0) {
                            mView.renderSources(dbManger.reorderByIndex(sourceList));
                        }
                    } else {
                        mView.renderSources(null);
                    }
                }, e -> {
                    mView.renderSources(null);
                    Timber.e(e);
                });

    }

    @Override
    public void loadTopics() {
        List<NewsTopicsRequest> topicsList = dbManger.getTopics(true);
        Observable.just(topicsList).subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(topicsList1 -> {
                    topicsList1 = new RealmManager().createCopy(topicsList1);
                    return Observable.just(topicsList1);
                }).observeOn(Schedulers.io()).flatMap(topicsList13 -> {
            JSONArray param = new JSONArray();
            for (NewsTopicsRequest topic : topicsList13) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "topic");
                jsonObject.put("query", topic.getQ());
                param.put(jsonObject);
            }
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (param).toString());
            return NetworkService.getNewsAPI().getFeatureImage(REQ_IMAGE_PROXY, body);
        }, (topicsList12, iconsBean) -> {
            HashMap<String, String> imgUrlMap = new HashMap<>();
            for (int i = 0; i < iconsBean.getSize(); i++) {
                IconsBean.DataBean bean = iconsBean.getData().get(i);
                imgUrlMap.put(bean.getSource(), bean.getImgUrl());
            }

            for (NewsTopicsRequest topic : topicsList12) {
                String topicName = topic.getQ();
                topic.setImgUrl(imgUrlMap.get(topicName));
            }
            return topicsList12;
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(newsTopicsRequests -> {
                    if (newsTopicsRequests != null && newsTopicsRequests.size() > 0) {
                        mView.renderSources(newsTopicsRequests);
                    } else {

                    }
                }, e -> {
                    mView.renderSources(null);
                    Timber.e(e);
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
