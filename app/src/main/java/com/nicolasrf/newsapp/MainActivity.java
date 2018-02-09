package com.nicolasrf.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Article>>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    private static final int ARTICLE_LOADER_ID = 1;
    private static final String THEGUARDIAN_REQUEST_URL =
            "https://content.guardianapis.com/search?q=";
    //por default el api muestra 10 resultados.

    SwipeRefreshLayout swipe;
    private TextView mEmptyStateTextView;
    private ArticleAdapter mAdapter;
    ProgressBar loadingIndicator;
    LoaderManager loaderManager;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        mEmptyStateTextView.setVisibility(View.VISIBLE);
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);

        swipe = findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnArticleClickListener(new ArticleAdapter.OnArticleClickListener() {
            @Override
            public void onArticleList(Article article) {
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri articleUri = Uri.parse(article.getArticleURL());
                // Create a new intent to view the article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        //****CHECK INTERNET CONNECTION AND INIT LOAD MANAGER.
        isConnected = checkInternetStatus();
        if(!isConnected){
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        } else{
            loadingIndicator.setVisibility(View.VISIBLE);
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        }


    }

    private boolean checkInternetStatus(){
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
        //get SharedPreferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //get preferences by key and set default values.
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        String sections = sharedPrefs.getString(
                getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_default)
        );
        Log.d(TAG, "onCreateLoader: orderBy" + orderBy);
        Log.d(TAG, "onCreateLoader: sections" + sections);

        //Ojo que para all news se pone en sections= un espacio vaciío!!

        String queryURI="";
        String queryURI2="";
        // Encode the URL to handle multi word searches and other characters
        queryURI = THEGUARDIAN_REQUEST_URL + "&" + orderBy + "&api-key=test";
        Log.d(TAG, "onCreateLoader: queryURI" + queryURI);
        queryURI2 = queryURI + "&" + sections;
        Log.d(TAG, "onCreateLoader: queryURI2" + queryURI2);

        //Call ArticleLoader to do fetch in background.
        return new ArticleLoader(this, queryURI2);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> articles) {
        swipe.setRefreshing(false);

        // Clear the adapter of previous earthquake data
        Log.d(TAG, "onLoadFinished: " + articles);
        mAdapter.clearAdapter();

        // If there is a valid list of {@link Articles}s, then add them to the adapter's
        // data set. This will trigger the RecyclerView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addArticles(articles);
            loadingIndicator.setVisibility(View.GONE);
            Log.d(TAG, "onLoadFinished: " + articles);
        }

    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.d(TAG, "onLoaderReset: ");
        mAdapter.clearAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    refresh method from Swipe
     */
    @Override
    public void onRefresh() {
        //restart load manager when swipe-
        if(!isConnected){
            mEmptyStateTextView.setText(R.string.no_internet_connection);
            loadingIndicator.setVisibility(View.GONE);
            //init loader
            loaderManager = getLoaderManager();
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
            //destroy loader
            loaderManager.destroyLoader(ARTICLE_LOADER_ID);
        }else{
            mEmptyStateTextView.setVisibility(View.INVISIBLE);
        }
        loaderManager.restartLoader(ARTICLE_LOADER_ID, null, this);

    }

    //ToDo. Aplicar progressBar al configurar las preferencias.
    //ToDo. Mostrar texto de no conexion a internet cuando se realiza el swipe sin internet.
}
