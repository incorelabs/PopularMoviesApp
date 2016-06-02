package com.example.android.popularmoviesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private int mSelectedOrder;
    private AlertDialog mSortDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_detail) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] sortBy = {"Popularity", "Top Rated"};

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mSelectedOrder = 0;

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    builder.setTitle("Sort By");
                    builder.setSingleChoiceItems(sortBy, mSelectedOrder, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MovieFragment movieFragment = new MovieFragment();
                            switch (which) {
                                case 0:
                                    mSelectedOrder = 0;
                                    mSortDialog.dismiss();
                                    movieFragment.updateMoviesList(0);
                                    Snackbar.make(view, "Updating list with Most Popular Movies", Snackbar.LENGTH_LONG)
                                            .show();
                                    break;
                                case 1:
                                    mSelectedOrder = 1;
                                    mSortDialog.dismiss();
                                    movieFragment.updateMoviesList(1);
                                    Snackbar.make(view, "Updating list with Top Rated Movies", Snackbar.LENGTH_LONG)
                                            .show();
                                    break;
                            }
                        }
                    });
                    mSortDialog = builder.create();
                    mSortDialog.show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            MovieFragment movieFragment = new MovieFragment();
            if (mSelectedOrder == 0 || mSelectedOrder == 1)
                movieFragment.updateMoviesList(mSelectedOrder);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(String intentType, Parcelable movieData) {
        // In two-pane mode, show the detail view in this activity by
        // adding or replacing the detail fragment using a
        // fragment transaction.
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_PARCEL, movieData);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(intentType, movieData);
            startActivity(intent);
        }

    }
}
