package com.nicolasrf.newsapp;

// THIS CLASS STORES THE BASE DATA FOR EACH NEWS ARTICLE

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

class Article {

    //FOR THE NEWS TITLE STRING
    private String mArticleTitle;

    //FOR THE NEWS SECTION STRING
    private String mArticleSection;

    //FOR THE PUBLISH TIME
    private String mArticlePublishTime;

    //FOR THE NEWS ARTICLE
    private String mArticleURL;

    public Article(String articleTitle, String articleSection, String articlePublishTime, String articleURL) {
        this.mArticleTitle = articleTitle;
        this.mArticleSection = articleSection;
        this.mArticlePublishTime = articlePublishTime;
        this.mArticleURL = articleURL;
    }

    //RETURNS THE ARTICLE TITLE
    @Nullable
    public String getArticleTitle() {
        return mArticleTitle;
    }

    //RETURNS THE ARTICLE SECTION
    @Nullable
    public String getArticleSection() {
        return mArticleSection;
    }

    //RETURNS THE ARTICLE PUBLISH TIME
    @Nullable
    public String getArticlePublishTime() {
        return mArticlePublishTime;
    }

    //RETURNS THE ARTICLE URL
    @Nullable
    public String getArticleURL() {
        return mArticleURL;
    }
}
