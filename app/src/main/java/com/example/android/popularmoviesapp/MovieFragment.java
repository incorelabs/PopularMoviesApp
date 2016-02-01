package com.example.android.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    private MovieListAdapter movieListAdapter;

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
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchMoviesTask moviesTask = new FetchMoviesTask();
            moviesTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<MovieCard> moviesList = new ArrayList<MovieCard>();

        movieListAdapter = new MovieListAdapter(getActivity(), moviesList);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_movie);
        listView.setAdapter(movieListAdapter);

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<MovieCard>> {
        ArrayList<MovieCard> moviesList = new ArrayList<>();

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private ArrayList<MovieCard> getMovieDataFromJson(String moviesJsonStr) throws JSONException {
            final String TMDB_MOVIE_LIST = "results";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_TITLE = "original_title";
            final String TMDB_USER_RATING = "vote_average";
            final String TMDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
            final String TMDB_POSTER_WIDTH = "w185";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesList = moviesJson.getJSONArray(TMDB_MOVIE_LIST);

            ArrayList<MovieCard> movieCards = new ArrayList<MovieCard>();

            for (int i = 0; i < moviesList.length(); i++) {
                movieCards.add(new MovieCard(
                                moviesList.getJSONObject(i).getString(TMDB_TITLE),
                                moviesList.getJSONObject(i).getString(TMDB_RELEASE_DATE),
                                Float.parseFloat(moviesList.getJSONObject(i).getString(TMDB_USER_RATING)),
                                TMDB_POSTER_BASE_URL + TMDB_POSTER_WIDTH + moviesList.getJSONObject(i).getString(TMDB_POSTER_PATH))
                );
            }
            return movieCards;
        }

        @Override
        protected ArrayList<MovieCard> doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String sortOrder = "popularity.desc"; // vote_average.desc

            try {
                // This is the url for v3 of The Movie DB API
                final String URL_PROTOCOL = "http";
                final String URL_HOSTNAME = "api.themoviedb.org";
                final String API_VERSION = "3";
                final String DISCOVER_PATH = "discover";
                final String MOVIE_PATH = "movie";
                final String SORT_PARAM = "sort_by";
                final String API_KEY = "api_key";

                /* URL -> http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc */

                /* URL -> http://api.themoviedb.org/3/movie/top_rated */

                Uri.Builder tmdbUri = new Uri.Builder();
                tmdbUri.scheme(URL_PROTOCOL)
                        .authority(URL_HOSTNAME)
                        .appendPath(API_VERSION)
                        .appendPath(DISCOVER_PATH)
                        .appendPath(MOVIE_PATH)
                        .appendQueryParameter(SORT_PARAM, sortOrder)
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
                movieListAdapter.clear();
                movieListAdapter.addAll(movieCards);
            }
        }
    }
}
