package com.example.android.popularmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchMoviesTask extends AsyncTask<Integer, Void, ArrayList<MovieCard>> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

    private final Context mContext;

    public FetchMoviesTask(Context context) {
        this.mContext = context;
    }

    private ArrayList<MovieCard> getMovieDataFromJson(String moviesJsonStr) throws JSONException {
        final String TMDB_MOVIE_LIST = "results";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_TITLE = "original_title";
        final String TMDB_MOVIE_ID = "id";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
        final String TMDB_POSTER_WIDTH = "w185";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(TMDB_MOVIE_LIST);

        ArrayList<MovieCard> movieCards = new ArrayList<MovieCard>();

        for (int i = 0; i < moviesList.length(); i++) {
            movieCards.add(
                    new MovieCard(
                            moviesList.getJSONObject(i).getInt(TMDB_MOVIE_ID),
                            (byte) 0,
                            moviesList.getJSONObject(i).getString(TMDB_TITLE),
                            moviesList.getJSONObject(i).getString(TMDB_RELEASE_DATE),
                            TMDB_POSTER_BASE_URL + TMDB_POSTER_WIDTH + moviesList.getJSONObject(i).getString(TMDB_POSTER_PATH),
                            moviesList.getJSONObject(i).getString(TMDB_OVERVIEW),
                            Float.parseFloat(moviesList.getJSONObject(i).getString(TMDB_USER_RATING))
                    )
            );
        }
        return movieCards;
    }

    @Override
    protected ArrayList<MovieCard> doInBackground(Integer... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // This is the url for v3 of The Movie DB API
            final String URL_PROTOCOL = "http";
            final String URL_HOSTNAME = "api.themoviedb.org";
            final String API_VERSION = "3";
            final String MOVIE_PATH = "movie";
            final String SORT_PARAM = params[0] == 0 ? "popular" : "top_rated";
            final String API_KEY = "api_key";

            /* URL -> http://api.themoviedb.org/3/movie/popular */

            /* URL -> http://api.themoviedb.org/3/movie/top_rated */

            Uri.Builder tmdbUri = new Uri.Builder();
            tmdbUri.scheme(URL_PROTOCOL)
                    .authority(URL_HOSTNAME)
                    .appendPath(API_VERSION)
                    .appendPath(MOVIE_PATH)
                    .appendPath(SORT_PARAM)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY);

            URL urlFromUri = new URL(tmdbUri.build().toString());
            Log.v("theMovieDatabase", urlFromUri.toString());

            // Open Connection -> Set Request Type -> Connect
            urlConnection = (HttpURLConnection) urlFromUri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the Input Stream from the URL into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMovieDataFromJson(moviesJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieCard> movieCards) {
        if (movieCards != null) {
            MovieFragment.mNoMoviesTextView.setText("");
            MovieFragment.mNoMoviesTextView.setVisibility(View.GONE);
            MovieFragment.mMovieListAdapter.clear();
            MovieFragment.mMovieListAdapter.addAll(movieCards);
        } else {
            MovieFragment.mNoMoviesTextView.setVisibility(View.VISIBLE);
            MovieFragment.mNoMoviesTextView.setText(R.string.no_movie);
        }
        MovieFragment.savedMoviesList = new TransferData(movieCards, TransferData.ARRAY_ADAPTER_TYPE);
    }
}