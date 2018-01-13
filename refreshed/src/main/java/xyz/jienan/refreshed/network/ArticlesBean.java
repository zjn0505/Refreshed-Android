package xyz.jienan.refreshed.network;

import java.util.Comparator;
import java.util.Date;

import xyz.jienan.refreshed.TimeUtils;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class ArticlesBean {
    /**
     * source : {"id":"the-washington-post","name":"The Washington Post"}
     * author : https://www.facebook.com/aaronblakewp?fref=ts
     * title : Trump just torpedoed his own administration's position on FISA
     * description : The White House reiterated its support for the program Wednesday night. Trump questioned that support Thursday morning.
     * url : https://www.washingtonpost.com/news/the-fix/wp/2018/01/11/trump-just-torpedoed-his-own-administrations-position-on-fisa/
     * urlToImage : https://www.washingtonpost.com/rf/image_1484w/2010-2019/Wires/Images/2017-09-29/Getty/855568760.jpg?t=20170517
     * publishedAt : 2018-01-11T14:28:05Z
     */

    private NewsSourceBean source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;

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


    public static class ReleaseComparator implements Comparator<ArticlesBean> {
        @Override
        public int compare(ArticlesBean o1, ArticlesBean o2) {
            Date d1 = TimeUtils.convertStringToDate(o1.getPublishedAt());
            Date d2 = TimeUtils.convertStringToDate(o2.getPublishedAt());
            if (d1 != null && d2 != null)
                return d1.compareTo(d2);
            else
                return 0;
        }
    }
}
