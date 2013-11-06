package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.idunnolol.images.R;
import com.idunnolol.images.content.RecentImagesSuggestionsProvider;
import com.idunnolol.images.utils.Ui;

import java.util.List;

public class ImagesActivity extends Activity implements ImageDataFragment.ImageDataFragmentListener,
        ImagesFragment.ImagesFragmentListener {

    private ImagesFragment mImagesFragment;
    private ImageDataFragment mDataFragment;

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

            // Load current data in data fragment into images fragment
            mImagesFragment.bind(mDataFragment.getImageUrls(), mDataFragment.canLoadMore());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.images, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When we open the search view, we want to show the current query
                mSearchView.setQuery(mDataFragment.getQuery(), false);
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchItem.collapseActionView();
                return false;
            }
        });

        return true;
    }

    // ImageDataFragmentListener

    @Override
    public void onImagesLoaded(List<String> imageUrls, boolean canLoadMore) {
        mImagesFragment.bind(imageUrls, canLoadMore);
    }

    // ImagesFragmentListener

    @Override
    public void onRequestMoreImages() {
        mDataFragment.requestMoreImages();
    }
}
