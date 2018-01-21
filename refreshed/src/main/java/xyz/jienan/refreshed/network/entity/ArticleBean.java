package xyz.jienan.refreshed.network.entity;

import java.util.Comparator;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import xyz.jienan.refreshed.TimeUtils;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class ArticleBean extends RealmObject {
    /**
     * source : {"id":"the-washington-post","name":"The Washington Post"}
     * author : https://www.facebook.com/aaronblakewp?fref=ts
     * title : Trump just torpedoed his own administration's position on FISA
     * description : The White House reiterated its support for the program Wednesday night. Trump questioned that support Thursday morning.
     * url : https://www.washingtonpost.com/news/the-fix/wp/2018/01/11/trump-just-torpedoed-his-own-administrations-position-on-fisa/
     * urlToImage : https://www.washingtonpost.com/rf/image_1484w/2010-2019/Wires/Images/2017-09-29/Getty/855568760.jpg?t=20170517
     * publishedAt : 2018-01-11T14:28:05Z
     */
    @Ignore
    private NewsSourceBean source;
    @Ignore
    private String author;
    @Ignore
    private String title;
    @Ignore
    private String description;
    @PrimaryKey
    @Required
    private String url;
    @Ignore
    private String urlToImage;
    @Ignore
    private String publishedAt;

    private int accessCount = 0;

    public NewsSourceBean getSource() {
        return source;
    }

    public void setSource(NewsSourceBean source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void increaseAccessCount() {
        accessCount++;
    }


    public static class ReleaseComparator implements Comparator<ArticleBean> {
        @Override
        public int compare(ArticleBean o1, ArticleBean o2) {
            Date d1 = TimeUtils.convertStringToDate(o1.getPublishedAt());
            Date d2 = TimeUtils.convertStringToDate(o2.getPublishedAt());
            if (d1 != null && d2 != null)
                return d2.compareTo(d1);
            else
                return 0;
        }
    }
}
