package com.wpfeedreader.android.Database;

import android.provider.BaseColumns;

/**
 * Created by Diego on 14/09/2017.
 */

public final class ReaderFavourites {
    private ReaderFavourites() {}

    /* Inner class that defines the table contents */
    public static class Favourites implements BaseColumns {
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_AUTHOR = "author";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SHAREURL = "shareurl";
        public static final String COLUMN_NAME_THUMBURL = "thumburl";
        public static final String COLUMN_NAME_IMAGEURL = "imageurl";
    }
}