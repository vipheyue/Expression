package com.lightworld.expression;

import java.util.List;

/**
 * Created by heyue on 2018/1/29.
 */

public class ExpressionBean {


    /**
     * title : dog
     * host : https://gitee.com/heyue/pic1/raw/master/dog/
     * listData : ["20180128220610_408.jpeg","20180128215841_142.jpeg","20180128221238_478.jpeg","20180128220052_76.png","20180128220834_705.png","20180128220132_642.png","20180128221613_849.jpeg","20180123032724_304.jpeg","20180123004849_506.png","20180123005102_743.png","20180123010809_662.png","20180123021521_132.png","20180123004211_275.png","20180123021330_210.png","20180123004106_534.jpeg","20180123003812_128.jpeg","20180123003323_384.png","20180123003513_892.png","20180123000826_782.png","20180123000857_170.png"]
     */

    private String title;
    private String host;
    private List<String> listData;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<String> getListData() {
        return listData;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }
}
