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

public class FetchReviewsTask extends AsyncTask<Integer, Void, ArrayList<ReviewCard>> {
    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final Context mContext;
    private View mRootView;

    public FetchReviewsTask(Context context, View rootView) {
        this.mContext = context;
        this.mRootView = rootView;
    }

    private ArrayList<ReviewCard> getReviewDataFromJson(String reviewsJsonStr) throws JSONException {
        final String TMDB_REVIEW_LIST = "results";
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsList = reviewsJson.getJSONArray(TMDB_REVIEW_LIST);

        ArrayList<ReviewCard> reviewCards = new ArrayList<ReviewCard>();

        for (int i = 0; i < reviewsList.length(); i++) {
            reviewCards.add(
                    new ReviewCard(
                            (i + 1) + ". " + reviewsList.getJSONObject(i).getString(TMDB_REVIEW_AUTHOR),
                            reviewsList.getJSONObject(i).getString(TMDB_REVIEW_CONTENT)
                    )
            );
        }
        return reviewCards;
    }

    @Override
    protected ArrayList<ReviewCard> doInBackground(Integer... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewsJsonStr = null;

        try {
            // This is the url for v3 of The Movie DB API
            final String URL_PROTOCOL = "http";
            final String URL_HOSTNAME = "api.themoviedb.org";
            final String API_VERSION = "3";
            final String MOVIE_PATH = "movie";
            final String MOVIE_ID = params[0].toString();
            final String MOVIE_METHOD_TYPE = "reviews";
            final String API_KEY = "api_key";

            /* URL -> http://api.themoviedb.org/3/movie/{id}/reviews */

            Uri.Builder tmdbUri = new Uri.Builder();
            tmdbUri.scheme(URL_PROTOCOL)
                    .authority(URL_HOSTNAME)
                    .appendPath(API_VERSION)
                    .appendPath(MOVIE_PATH)
                    .appendPath(MOVIE_ID)
                    .appendPath(MOVIE_METHOD_TYPE)
                    .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY);

            URL urlFromUri = new URL(tmdbUri.build().toString());
            Log.v("theMovieReviewDatabase", urlFromUri.toString());

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
            reviewsJsonStr = buffer.toString();
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
            return getReviewDataFromJson(reviewsJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<ReviewCard> reviewCards) {
        if (reviewCards != null && reviewCards.size() > 0) {
            DetailFragment.mReviewListAdapter.clear();
            DetailFragment.mReviewListAdapter.addAll(reviewCards);
        }
    }
}
