package com.example.android.popularmoviesapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private int mSelectedOrder;
    private AlertDialog mSortDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                            switch (which) {
                                case 0:
                                    mSelectedOrder = 0;
                                    mSortDialog.dismiss();
                                    MovieFragment.updateMoviesList(0);
                                    Snackbar.make(view, "Updating list with Most Popular Movies", Snackbar.LENGTH_LONG)
                                            .show();
                                    break;
                                case 1:
                                    mSelectedOrder = 1;
                                    mSortDialog.dismiss();
                                    MovieFragment.updateMoviesList(1);
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
            MovieFragment.updateMoviesList(mSelectedOrder);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
