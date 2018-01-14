package xyz.jienan.refreshed.network;

import java.util.List;


/**
 * Created by Jienan on 2017/7/24.
 */

public class NewsSourcesBean {


    /**
     * status : ok
     * sources : [{"id":"abc-news-au","name":"ABC News (AU)","description":"Australia's most trusted source of local, national and world news. Comprehensive, independent, in-depth analysis, the latest business, sport, weather and more.","url":"http://www.abc.net.au/news","category":"general","language":"en","country":"au","urlsToLogos":{"small":"","medium":"","large":""},"sortBysAvailable":["top"]}]
     */

    private String status;
    private List<NewsSourceBean> sources;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NewsSourceBean> getSources() {
        return sources;
    }

    public void setSources(List<NewsSourceBean> sources) {
        this.sources = sources;
    }

    public static class SourcesBean {
        /**
         * id : abc-news-au
         * name : ABC News (AU)
         * description : Australia's most trusted source of local, national and world news. Comprehensive, independent, in-depth analysis, the latest business, sport, weather and more.
         * url : http://www.abc.net.au/news
         * category : general
         * language : en
         * country : au
         * urlsToLogos : {"small":"","medium":"","large":""}
         * sortBysAvailable : ["top"]
         */

        private String id;
        private String name;
        private String description;
        private String url;
        private String category;
        private String language;
        private String country;
        private UrlsToLogosBean urlsToLogos;
        private List<String> sortBysAvailable;

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

        public UrlsToLogosBean getUrlsToLogos() {
            return urlsToLogos;
        }

        public void setUrlsToLogos(UrlsToLogosBean urlsToLogos) {
            this.urlsToLogos = urlsToLogos;
        }

        public List<String> getSortBysAvailable() {
            return sortBysAvailable;
        }

        public void setSortBysAvailable(List<String> sortBysAvailable) {
            this.sortBysAvailable = sortBysAvailable;
        }

        public static class UrlsToLogosBean {
            /**
             * small :
             * medium :
             * large :
             */

            private String small;
            private String medium;
            private String large;

            public String getSmall() {
                return small;
            }

            public void setSmall(String small) {
                this.small = small;
            }

            public String getMedium() {
                return medium;
            }

            public void setMedium(String medium) {
                this.medium = medium;
            }

            public String getLarge() {
                return large;
            }

            public void setLarge(String large) {
                this.large = large;
            }
        }
    }
}
