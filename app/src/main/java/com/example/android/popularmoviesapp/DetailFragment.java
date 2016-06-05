package com.example.android.popularmoviesapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract.FavouriteMoviesEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    protected static final String DETAIL_PARCEL = "PARCEL";

    protected static TrailerListAdapter mTrailerListAdapter;
    protected static ReviewListAdapter mReviewListAdapter;

    private AlertDialog mTrailerActionDialog;

    MovieCard movieCard;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();

        if (arguments != null) {
            movieCard = arguments.getParcelable(DetailFragment.DETAIL_PARCEL);
        }

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        ArrayList<TrailerCard> trailersList = new ArrayList<TrailerCard>();

        ArrayList<ReviewCard> reviewsList = new ArrayList<ReviewCard>();

        if (movieCard != null) {
            Picasso.with(getContext())
                    .load(movieCard.moviePosterUrl)
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    .into((ImageView) rootView.findViewById(R.id.detail_movie_poster));

            ((TextView) rootView.findViewById(R.id.detail_text)).setText(movieCard.movieTitle);

            ((ImageView) rootView.findViewById(R.id.detail_icon_rating)).setImageResource(R.drawable.ic_whatshot_black_18dp);
            ((TextView) rootView.findViewById(R.id.detail_rating)).setText(Float.toString(movieCard.movieRating));

            ((ImageView) rootView.findViewById(R.id.detail_icon_date)).setImageResource(R.drawable.ic_date_range_black_18dp);
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movieCard.movieReleaseDate);

            final ImageView favourite_btn = (ImageView) rootView.findViewById(R.id.favourite_btn);
            if (movieCard.isFavourite == 1) {
                favourite_btn.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                favourite_btn.setImageResource(android.R.drawable.btn_star_big_off);
            }
            favourite_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (movieCard.isFavourite == 1) {
                        removeFromFavourites();
                        favourite_btn.setImageResource(android.R.drawable.btn_star_big_off);
                    } else {
                        addToFavourites();
                        favourite_btn.setImageResource(android.R.drawable.btn_star_big_on);
                    }
                }
            });

            ((TextView) rootView.findViewById(R.id.plot_label)).setText(R.string.label_overview);

            ((TextView) rootView.findViewById(R.id.detail_movie_overview)).setText(movieCard.movieOverview);

            FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(getActivity(), rootView);
            fetchTrailersTask.execute(movieCard.movieId);

            mTrailerListAdapter = new TrailerListAdapter(getActivity(), trailersList);
            ListView trailersListView = (ListView) rootView.findViewById(R.id.trailers_list_view);
            trailersListView.setAdapter(mTrailerListAdapter);

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final CharSequence[] trailerActions = {"Watch", "Share"};

            trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final String trailerTitle = mTrailerListAdapter.getItem(position).trailerTitle;
                    final String trailerKey = mTrailerListAdapter.getItem(position).trailerKey;

                    builder.setTitle(trailerTitle);
                    builder.setSingleChoiceItems(trailerActions, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    mTrailerActionDialog.dismiss();
                                    // Launch Intent to open youtube app.
                                    watchYoutubeVideo(trailerKey);
                                    break;
                                case 1:
                                    mTrailerActionDialog.dismiss();
                                    // Launch Intent to share the trailer.
                                    shareIntent("Check this Trailer for the Movie: " + movieCard.movieTitle
                                            + ". \n" + "Link: http://www.youtube.com/watch?v=" + trailerKey);
                                    break;
                            }
                        }
                    });
                    mTrailerActionDialog = builder.create();
                    mTrailerActionDialog.show();
                }
            });

            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity(), rootView);
            fetchReviewsTask.execute(movieCard.movieId);

            mReviewListAdapter = new ReviewListAdapter(getActivity(), reviewsList);
            ListView reviewsListView = (ListView) rootView.findViewById(R.id.reviews_list_view);
            reviewsListView.setAdapter(mReviewListAdapter);
        }

        return rootView;
    }

    public void watchYoutubeVideo(String trailerKey) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));
            startActivity(intent);
        }
    }

    public void shareIntent(String sharedContent) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedContent);
        startActivity(sendIntent);
    }

    private void addToFavourites() {
        ContentValues movieCardValues = new ContentValues();

        movieCardValues.put(FavouriteMoviesEntry.COLUMN_MOVIE_ID, movieCard.movieId);
        movieCardValues.put(FavouriteMoviesEntry.COLUMN_TITLE, movieCard.movieTitle);
        movieCardValues.put(FavouriteMoviesEntry.COLUMN_POSTER, movieCard.moviePosterUrl);
        movieCardValues.put(FavouriteMoviesEntry.COLUMN_SYNOPSIS, movieCard.movieOverview);
        movieCardValues.put(FavouriteMoviesEntry.COLUMN_USER_RATING, movieCard.movieRating);
        movieCardValues.put(FavouriteMoviesEntry.COLUMN_RELEASE_DATE, movieCard.movieReleaseDate);

        Uri insertedUri = getContext().getContentResolver()
                .insert(FavouriteMoviesEntry.CONTENT_URI, movieCardValues);

        if (insertedUri != null)
            movieCard.isFavourite = 1;
    }

    public void removeFromFavourites() {
        int rowsDeleted = getContext().getContentResolver()
                .delete(FavouriteMoviesEntry.CONTENT_URI, FavouriteMoviesEntry.COLUMN_MOVIE_ID + " = ?", new String[]{Integer.toString(movieCard.movieId)});

        if (rowsDeleted > 0)
            movieCard.isFavourite = 0;
    }
}
