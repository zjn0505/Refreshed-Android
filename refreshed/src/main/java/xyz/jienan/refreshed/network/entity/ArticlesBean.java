package xyz.jienan.refreshed.network.entity;

import java.util.List;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class ArticlesBean {

    /**
     * status : ok
     * totalResults : 20
     */

    private String status;
    private int totalResults;
    private List<ArticleBean> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<ArticleBean> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleBean> articles) {
        this.articles = articles;
    }
}
