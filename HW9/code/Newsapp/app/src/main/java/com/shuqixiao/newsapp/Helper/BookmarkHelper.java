package com.shuqixiao.newsapp.Helper;

import com.google.gson.Gson;
import com.shuqixiao.newsapp.Entity.NewsItem;

public class BookmarkHelper {
    private Gson gson;

    public BookmarkHelper() {
        this.gson = new Gson();
    }

    public String newsItem2String(NewsItem newsItem) {
        String jsonObject = gson.toJson(newsItem);
        return jsonObject;
    }

    public NewsItem string2NewsItem(String jsonString) {
        NewsItem newsItem = gson.fromJson(jsonString, NewsItem.class);
        return newsItem;
    }

}
