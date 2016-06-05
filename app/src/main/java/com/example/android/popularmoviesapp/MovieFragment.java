package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {

    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    protected static MovieListAdapter mMovieListAdapter;
    protected static GridView mGridView;
    protected static TransferData savedMoviesList;
    protected static TextView mNoMoviesTextView;

    private static String mOutStateMovieListKey = "savedMoviesList";
    private static String mOutStateAdapterKey = "savedMoviesAdapter";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(String intentType, Parcelable movieData);
    }

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<MovieCard> moviesList = null;

        boolean isArrayAdapter = false;

        if (savedInstanceState != null && savedInstanceState.containsKey(mOutStateMovieListKey)) {
            if (savedInstanceState.getInt(mOutStateAdapterKey) == TransferData.ARRAY_ADAPTER_TYPE) {
                moviesList = savedInstanceState.getParcelableArrayList(mOutStateMovieListKey);
                isArrayAdapter = true;
            }
        } else if (savedMoviesList != null && savedMoviesList.adapterType == TransferData.ARRAY_ADAPTER_TYPE) {
            ArrayList<MovieCard> moviesListFromObject = (ArrayList<MovieCard>) savedMoviesList.savedMoviesList;
            if (moviesListFromObject != null && moviesListFromObject.size() > 0) {
                moviesList = moviesListFromObject;
            } else {
                moviesList = new ArrayList<MovieCard>();
            }
            isArrayAdapter = true;
        } else {
            moviesList = new ArrayList<MovieCard>();
            isArrayAdapter = true;
        }

        mNoMoviesTextView = (TextView) rootView.findViewById(R.id.no_movie_textview);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        mGridView.setDrawSelectorOnTop(true);

        if (isArrayAdapter) {
            if (moviesList.size() > 0) {
                mNoMoviesTextView.setText("");
                mNoMoviesTextView.setVisibility(View.GONE);
            } else {
                mNoMoviesTextView.setVisibility(View.VISIBLE);
                mNoMoviesTextView.setText(R.string.no_movie);
            }

            mMovieListAdapter = new MovieListAdapter(getActivity(), moviesList);

            mGridView.setAdapter(mMovieListAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // The getItem method returns a MovieCard type of Object.
                    ((Callback) getActivity())
                            .onItemSelected(Intent.EXTRA_TEXT, mMovieListAdapter.getItem(position));
                }
            });
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (savedMoviesList == null) {
            updateMoviesList(0);
        } else if (savedMoviesList != null && savedMoviesList.adapterType == TransferData.CURSOR_ADAPTER_TYPE) {
            cursorGridView(getContext(), (Uri) savedMoviesList.savedMoviesList);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (savedMoviesList != null) {
            if (savedMoviesList.adapterType == TransferData.ARRAY_ADAPTER_TYPE) {
                outState.putParcelableArrayList(mOutStateMovieListKey, (ArrayList<MovieCard>) savedMoviesList.savedMoviesList);
            } else {
                outState.putParcelable(mOutStateMovieListKey, (Uri) savedMoviesList.savedMoviesList);
            }
            outState.putInt(mOutStateAdapterKey, savedMoviesList.adapterType);
        }

        super.onSaveInstanceState(outState);
    }

    public void updateMoviesList(int sortOrder) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute(sortOrder);
    }

    public void switchGridViewAdapter(final Context context, boolean isCursorAdapter, int sortOrder) {
        if (isCursorAdapter) {
            Uri favouriteMoviesUri = MovieContract.FavouriteMoviesEntry.buildFavouriteMoviesCollection();
            cursorGridView(context, favouriteMoviesUri);
            savedMoviesList = new TransferData(favouriteMoviesUri, TransferData.CURSOR_ADAPTER_TYPE);
        } else {
            mMovieListAdapter.clear();
            mGridView.setAdapter(mMovieListAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // The getItem method returns a MovieCard type of Object.
                    ((Callback) context)
                            .onItemSelected(Intent.EXTRA_TEXT, mMovieListAdapter.getItem(position));
                }
            });
            updateMoviesList(sortOrder);
        }
    }

    private MovieCard cursorToMovieCard(Cursor cursor) {
        int movieId = cursor.getInt(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_MOVIE_ID)
        );

        byte isFavourite = 1;

        String movieTitle = cursor.getString(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_TITLE)
        );

        String movieReleaseDate = cursor.getString(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_RELEASE_DATE)
        );

        String moviePosterUrl = cursor.getString(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_POSTER)
        );

        String movieOverview = cursor.getString(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_SYNOPSIS)
        );

        float movieRating = cursor.getFloat(
                cursor.getColumnIndex(MovieContract.FavouriteMoviesEntry.COLUMN_USER_RATING)
        );

        MovieCard movieCard = new MovieCard(
                movieId, isFavourite, movieTitle, movieReleaseDate, moviePosterUrl, movieOverview, movieRating
        );

        return movieCard;
    }

    private void cursorGridView(final Context context, Uri favouriteMoviesUri) {
        Cursor cursor = context.getContentResolver().query(
                favouriteMoviesUri,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            mNoMoviesTextView.setText("");
            mNoMoviesTextView.setVisibility(View.GONE);
        } else {
            mNoMoviesTextView.setVisibility(View.VISIBLE);
            mNoMoviesTextView.setText(R.string.no_favourite_movie);
        }

        FavouriteMovieCursorAdapter favouriteMovieCursorAdapter = new FavouriteMovieCursorAdapter(context, cursor, 0);
        mGridView.setAdapter(favouriteMovieCursorAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                MovieCard movieCard = null;
                if (cursor != null) {
                    movieCard = cursorToMovieCard(cursor);
                }

                ((Callback) context)
                        .onItemSelected(Intent.EXTRA_TEXT, movieCard);
            }
        });
    }
}
