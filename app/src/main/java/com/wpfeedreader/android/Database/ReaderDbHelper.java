package com.wpfeedreader.android.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.wpfeedreader.android.Database.ReaderFavourites.Favourites;

/**
 * Created by Diego on 14/09/2017.
 */

public class ReaderDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Favourites.TABLE_NAME + " (" +
                    Favourites._ID + " INTEGER PRIMARY KEY," +
                    Favourites.COLUMN_NAME_ID + " INTEGER UNIQUE," +
                    Favourites.COLUMN_NAME_TITLE + " TEXT," +
                    Favourites.COLUMN_NAME_AUTHOR + " TEXT," +
                    Favourites.COLUMN_NAME_CONTENT + " TEXT," +
                    Favourites.COLUMN_NAME_SHAREURL + " TEXT," +
                    Favourites.COLUMN_NAME_THUMBURL + " TEXT," +
                    Favourites.COLUMN_NAME_IMAGEURL + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Favourites.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;     // If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "WordpressFeedReader.db";

    public ReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}