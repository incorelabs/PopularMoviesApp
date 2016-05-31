package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieFragment extends Fragment {
    private final String LOG_TAG = MovieFragment.class.getSimpleName();

    protected static MovieListAdapter movieListAdapter;
    protected static ArrayList<MovieCard> savedMoviesList;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("savedMoviesList")) {
            savedMoviesList = savedInstanceState.getParcelableArrayList("savedMoviesList");
        }

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

        final ArrayList<MovieCard> moviesList;

        if (savedInstanceState != null && savedInstanceState.containsKey("savedMoviesList")) {
            moviesList = savedInstanceState.getParcelableArrayList("savedMoviesList");
        } else {
            moviesList = new ArrayList<MovieCard>();
        }

        movieListAdapter = new MovieListAdapter(getActivity(), moviesList);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movie);
        gridView.setAdapter(movieListAdapter);
        gridView.setDrawSelectorOnTop(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The getItem method returns a MovieCard type of Object.
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, movieListAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (savedMoviesList == null) {
            updateMoviesList(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("savedMoviesList", savedMoviesList);
        super.onSaveInstanceState(outState);
    }

    public void updateMoviesList(int sortOrder) {
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute(sortOrder);
    }
}
