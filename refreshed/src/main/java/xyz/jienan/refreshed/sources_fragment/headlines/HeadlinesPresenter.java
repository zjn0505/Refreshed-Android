package xyz.jienan.refreshed.sources_fragment.headlines;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.network.entity.NewsSourcesBean;

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
        sourcesObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sources -> {
                    if (sources != null) {
                        List<NewsSourceBean> sourceList = sources.getSources();
                        if (sourceList != null && sourceList.size() > 0) {
                            sourceList = dbManger.reorderByIndex(sourceList);
                            int i = 0;
                            for (NewsSourceBean bean : sourceList) {
                                if (bean.getIndex() < 0) {
                                    break;
                                }
                                i++;
                            }

                            mView.renderSources(sourceList.subList(0, i));
                        }
                    } else {
                        mView.renderSources(null);
                    }
                }, e -> {
                    mView.renderSources(null);
                    e.printStackTrace();
                });

    }
}
