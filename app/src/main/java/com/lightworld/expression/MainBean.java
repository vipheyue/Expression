package com.lightworld.expression;

import java.util.List;

/**
 * Created by heyue on 2018/1/29.
 */

public class MainBean {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * title : 装逼
         * url : http://heyue.oss-cn-hangzhou.aliyuncs.com/AppData/expression/pic1/zhangbi.json
         */

        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
