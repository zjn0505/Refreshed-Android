package xyz.jienan.refreshed.source_select;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import xyz.jienan.refreshed.base.IDBManager;
import xyz.jienan.refreshed.base.RealmManager;
import xyz.jienan.refreshed.network.NetworkService;
import xyz.jienan.refreshed.network.NewsSourceBean;
import xyz.jienan.refreshed.network.NewsSourcesBean;

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
        sourcesObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<NewsSourcesBean>() {
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

    @Override
    public void loadThumbnails(List<String> names) {
        NetworkService.getNewsAPI().getFeatureImage()
    }


}
