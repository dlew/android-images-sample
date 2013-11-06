package com.idunnolol.images.content;

import android.content.SearchRecentSuggestionsProvider;

public class RecentImagesSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "com.idunnolol.images";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentImagesSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
