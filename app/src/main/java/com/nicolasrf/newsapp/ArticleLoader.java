package com.nicolasrf.newsapp;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads a list of articles by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class ArticleLoader extends AsyncTaskLoader<ArrayList<Article>> {

    /** Tag for log messages */
    private static final String TAG = "ArticleLoader";

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link ArticleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<Article> loadInBackground() {
        Log.d(TAG, "loadInBackground: " + mUrl);
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of articles.
        ArrayList<Article> articles = QueryUtils.fetchArticleData(mUrl);
        Log.d(TAG, "loadInBackground: " + articles);
        return articles;

    }
}
