package com.lee.nytimessearch;

import org.parceler.Parcel;

/**
 * Created by lee on 7/30/16.
 */
@Parcel
public class Filter{
    public String getSort() {
        return sort;
    }

//    public String[] getNewsDesk() {
//        return newsDesk;
//    }

//    public Date getBeginDate() {
//        return beginDate;
//    }

    public String sort;
//    Date beginDate;
//    String[] newsDesk;

    public Filter() {
        this.sort = "newest";
//        this.beginDate = null;
//        this.newsDesk = new String[0];
    }
}
