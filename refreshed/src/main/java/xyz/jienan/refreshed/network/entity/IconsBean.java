package xyz.jienan.refreshed.network.entity;

import java.util.List;

/**
 * Created by Jienan on 2018/1/19.
 */

public class IconsBean {

    /**
     * size : 1
     * data : [{"source":"msn","imgUrl":"https://media.glassdoor.com/sqll/519673/msn-labs-hyderabad-squarelogo-1464598606285.png"}]
     */

    private int size;
    private List<DataBean> data;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * source : msn
         * imgUrl : https://media.glassdoor.com/sqll/519673/msn-labs-hyderabad-squarelogo-1464598606285.png
         */

        private String source;
        private String imgUrl;

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }
}
