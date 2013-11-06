package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.widget.SearchView;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.idunnolol.images.Log;
import com.idunnolol.images.R;
import com.idunnolol.images.content.RecentImagesSuggestionsProvider;

import org.json.JSONObject;

public class ImagesActivity extends Activity {

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DiskBasedCache cache = new DiskBasedCache(getCacheDir());
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);

        setContentView(R.layout.activity_images);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ImagesFragment())
                    .commit();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String query = intent.getStringExtra(SearchManager.QUERY);

        // Save search to recents
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                RecentImagesSuggestionsProvider.AUTHORITY, RecentImagesSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(query, null);

        // Construct the URL
        Uri.Builder uriBuilder = Uri.parse("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8")
                .buildUpon();
        uriBuilder.appendQueryParameter("q", query);

        mRequestQueue.add(new JsonObjectRequest(Request.Method.GET, uriBuilder.build().toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i("Response: " + jsonObject);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("VolleyError: " + volleyError);
                    }
                }
        ));

        Log.i("Received search string: " + query);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mRequestQueue.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mRequestQueue.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

}
