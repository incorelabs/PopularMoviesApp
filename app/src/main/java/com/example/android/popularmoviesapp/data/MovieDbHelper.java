package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmoviesapp.data.MovieContract.FavouriteMoviesEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold the user's favourite movies.
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE = "CREATE TABLE " + FavouriteMoviesEntry.TABLE_NAME + " (" +
                FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavouriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_TITLE + " TEXT, " +
                FavouriteMoviesEntry.COLUMN_POSTER + " TEXT, " +
                FavouriteMoviesEntry.COLUMN_SYNOPSIS + " TEXT, " +
                FavouriteMoviesEntry.COLUMN_USER_RATING + " REAL, " +
                FavouriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Leaving out the upgrade policy for now as it is not required.
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
