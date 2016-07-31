package com.lee.nytimessearch;

import org.parceler.Parcel;

import java.util.Calendar;

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

    public Calendar getBeginDate() {
        return beginDate;
    }

    public String sort;
    public Calendar beginDate;
//    String[] newsDesk;

    public Filter() {
        this.sort = "newest";
        this.beginDate = null;
//        this.newsDesk = new String[0];
    }
}
