package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.widget.SearchView;

import com.android.volley.RequestQueue;
import com.idunnolol.images.R;
import com.idunnolol.images.content.RecentImagesSuggestionsProvider;
import com.idunnolol.images.utils.Ui;
import com.idunnolol.images.utils.VolleyUtils;

import java.util.List;

public class ImagesActivity extends Activity implements ImageDataFragment.ImageDataFragmentListener {

    private RequestQueue mRequestQueue;

    private ImagesFragment mImagesFragment;
    private ImageDataFragment mDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestQueue = VolleyUtils.createRequestQueue(this);

        setContentView(R.layout.activity_images);

        if (savedInstanceState == null) {
            mImagesFragment = new ImagesFragment();
            mDataFragment = new ImageDataFragment();

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, mImagesFragment, ImagesFragment.TAG)
                    .add(mDataFragment, ImageDataFragment.TAG)
                    .commit();
        }
        else {
            mImagesFragment = Ui.findFragment(this, ImagesFragment.TAG);
            mDataFragment = Ui.findFragment(this, ImageDataFragment.TAG);
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

        // Set the new query
        mDataFragment.setQuery(query);
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

    // ImageDataFragmentListener

    @Override
    public void onImagesLoaded(List<String> imageUrls, boolean canLoadMore) {
        mImagesFragment.bind(imageUrls, canLoadMore);
    }

    @Override
    public void onImageLoadError() {
        // TODO
    }
}
