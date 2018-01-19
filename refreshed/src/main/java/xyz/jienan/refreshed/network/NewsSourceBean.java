package xyz.jienan.refreshed.network;

import java.util.Comparator;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Jienan on 2017/7/24.
 */

public class NewsSourceBean extends RealmObject {


    public NewsSourceBean() {

    }

    public NewsSourceBean(String id) {
        this.id = id;
    }

    /**
     * id : abc-news-au
     * name : ABC News (AU)
     * description : Australia's most trusted source of local, national and world news. Comprehensive, independent, in-depth analysis, the latest business, sport, weather and more.
     * url : http://www.abc.net.au/news
     * category : general
     * language : en
     * country : au
     */
    @PrimaryKey
    private String id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;
    private String imgUrl;
    private int index = -1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public static class SourceIndexComparator implements Comparator<NewsSourceBean> {


        @Override
        public int compare(NewsSourceBean o1, NewsSourceBean o2) {
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
        if (obj != null && obj instanceof NewsSourceBean) {
            return ((NewsSourceBean) obj).getId().equals(this.getId());
        }
        return false;
    }
}
