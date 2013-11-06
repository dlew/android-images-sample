package com.idunnolol.images.ui;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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

            (new LoadFirstSuggestionTask()).execute();
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

        search(intent.getStringExtra(SearchManager.QUERY));
    }

    private void search(String query) {
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

    // A simple AsyncTask for load

    private class LoadFirstSuggestionTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... params) {
            // We have to act like the SearchManager here in order to query the last search...
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchableInfo searchable = searchManager.getSearchableInfo(getComponentName());

            Uri.Builder uriBuilder = new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_CONTENT)
                    .authority(RecentImagesSuggestionsProvider.AUTHORITY);
            uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);
            uriBuilder.appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT, String.valueOf(1));
            Uri uri = uriBuilder.build();

            String selection = searchable.getSuggestSelection();
            String[] selArgs = new String[] { "" };

            return getContentResolver().query(uri, null, selection, selArgs, null);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor.getCount() == 0) {
                // If there are no recent searches, search for "Android" by default
                search(getString(R.string.default_search));
            }
            else {
                cursor.moveToFirst();
                int queryIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_QUERY);
                search(cursor.getString(queryIndex));
            }
        }
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
