package xyz.jienan.refreshed.network.entity;

import java.util.Comparator;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import xyz.jienan.refreshed.R;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class NewsTopicsRequest extends RealmObject implements ITabEntity {

    @Override
    public int getType() {
        return R.integer.type_topic;
    }

    @PrimaryKey
    private String q;

    private String category;

    private String country = "us";

    private String language = "en";

    private int index = -1;

    @Override
    public String getName() {
        return q;
    }

    @Override
    public String getId() {
        return q;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static class TopicIndexComparator implements Comparator<NewsTopicsRequest> {


        @Override
        public int compare(NewsTopicsRequest o1, NewsTopicsRequest o2) {
            int i1 = o1.getIndex();
            int i2 = o2.getIndex();
            if (i1 == -1 && i2 == -1) {
                return 0;
            } else if (i1 == -1) {
                return 1;
            } else if (i2 == -1) {
                return -1;
            } else {
                return o1.getIndex() - o2.getIndex();
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof NewsTopicsRequest) {
            return ((NewsTopicsRequest) obj).getQ().equals(this.getQ());
        }
        return false;
    }
}
