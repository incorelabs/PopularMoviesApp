package com.example.android.popularmoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper movieDbHelper;

    static final int FAVOURITE_MOVIES = 100;
    static final int MOVIE_DETAILS = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_FAVOURITE_MOVIES + "/#", MOVIE_DETAILS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor resultSetCursor;
        switch (sUriMatcher.match(uri)) {
            // "favourite_movies"
            case FAVOURITE_MOVIES: {
                resultSetCursor = movieDbHelper.getReadableDatabase().query(
                        MovieContract.FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "favourite_movies/#"
            case MOVIE_DETAILS: {
                resultSetCursor = getMovieDetails(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        resultSetCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultSetCursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                return MovieContract.FavouriteMoviesEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MovieContract.FavouriteMoviesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES: {
                long _id = db.insert(MovieContract.FavouriteMoviesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.FavouriteMoviesEntry.buildFavouriteMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int rowsDeleted;

        if (selection == null)
            selection = "1";

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                rowsDeleted = db.delete(MovieContract.FavouriteMoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case FAVOURITE_MOVIES:
                rowsUpdated = db.update(MovieContract.FavouriteMoviesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private Cursor getMovieDetails(Uri uri, String[] projection, String sortOrder) {
        String movie_id = MovieContract.FavouriteMoviesEntry.getMovieIdFromUri(uri);

        String selection = MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID + " = ?";
        String[] selectionArgs = new String[]{movie_id};

        return movieDbHelper.getReadableDatabase().query(
                MovieContract.FavouriteMoviesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
}
