package com.shuqixiao.newsapp.Entity;

import android.net.Uri;


public class NewsItem {
    public String id;
    public String title;
    public String image;
    public String date;
    public String section;

    public NewsItem() {}

    public NewsItem(String id, String title, String date, String section) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.section = section;
    }
}
