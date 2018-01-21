package xyz.jienan.refreshed.base;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import xyz.jienan.refreshed.network.bean.NewsSourceBean;

/**
 * Created by jienanzhang on 15/01/2018.
 */

public class RealmManager implements IDBManager {

    private final static String[] RECOMMEND_SOURCES = new String[]{"ars-technica", "cnbc", "espn", "polygon"};

    private Realm realm;

    public RealmManager() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public List<NewsSourceBean> reorderByIndex(List<NewsSourceBean> sourceList) {
        boolean hasValidSources = false;
        if (realm.where(NewsSourceBean.class).findAll().size() == 0) {
            realm.beginTransaction();
            realm.insert(sourceList);
            realm.commitTransaction();
        } else {
            RealmResults<NewsSourceBean> result = realm.where(NewsSourceBean.class).greaterThan("index", -1).findAll().sort("index");
            if (result != null && result.size() != 0) {
                for (NewsSourceBean source : sourceList) {
                    if (result != null && result.contains(source)) {
                        NewsSourceBean sourceDB = result.where().equalTo("id", source.getId()).findFirst();
                        if (sourceDB != null) {
                            source.setIndex(result.indexOf(sourceDB));
                        }
                    } else {
                        source.setIndex(-1);
                    }
                }
                hasValidSources = true;
            }
        }
        // Should only used in FRE
        if (!hasValidSources) {
            for (int i = 0; i < RECOMMEND_SOURCES.length; i++) {
                String recSource = RECOMMEND_SOURCES[i];
                final NewsSourceBean sourceDB = realm.where(NewsSourceBean.class).equalTo("id", recSource).findFirst();
                if (sourceDB != null) {
                    final int finalI = i;
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            sourceDB.setIndex(finalI);
                        }
                    });
                    int position = sourceList.indexOf(new NewsSourceBean(recSource));
                    sourceList.get(position).setIndex(i);
                }
            }
        }

        Collections.sort(sourceList, new NewsSourceBean.SourceIndexComparator());
        return sourceList;
    }

    @Override
    public int updateIndex(List<NewsSourceBean> sourceList, boolean wasSelected, int position) {
        int toPosition = 0;
        final NewsSourceBean beanDB = realm.where(NewsSourceBean.class).equalTo("id", sourceList.get(position).getId()).findFirst();
        if (beanDB != null) {
            realm.beginTransaction();
            if (wasSelected) {
                // Unselect, move from position to the first candidate
                for (int i = position; i < sourceList.size(); i++) {
                    NewsSourceBean bean = sourceList.get(i);
                    if (i == position) {
                        bean.setIndex(-1);
                    } else if (bean.getIndex() == -1) {
                        break;
                    } else {
                        bean.setIndex(i - 1);
                    }
                    toPosition = i;
                }
                realm.insertOrUpdate(sourceList);
            } else {
                // Select, move from position to last selected.
                int maxIndex = 0;
                for (NewsSourceBean bean : sourceList) {
                    if (bean.getIndex() > maxIndex) {
                        maxIndex = bean.getIndex();
                    }
                    if (bean.getIndex() == -1) {
                        break;
                    }
                }
                toPosition = maxIndex + 1;
                beanDB.setIndex(toPosition);
                realm.insertOrUpdate(beanDB);
            }
            realm.commitTransaction();
        }
        return toPosition;
    }
}
