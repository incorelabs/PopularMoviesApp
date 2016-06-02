package com.example.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    protected static final String DETAIL_PARCEL = "PARCEL";

    MovieCard movieCard;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (arguments != null) {
            movieCard = arguments.getParcelable(DetailFragment.DETAIL_PARCEL);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (movieCard != null) {
            Picasso.with(getContext()).load(movieCard.moviePosterUrl).into((ImageView) rootView.findViewById(R.id.detail_movie_poster));
            ((TextView) rootView.findViewById(R.id.detail_text)).setText(movieCard.movieTitle);
            ((TextView) rootView.findViewById(R.id.detail_rating)).setText(Float.toString(movieCard.movieRating));
            ((TextView) rootView.findViewById(R.id.detail_release_date)).setText(movieCard.movieReleaseDate);
            ((TextView) rootView.findViewById(R.id.detail_movie_overview)).setText(movieCard.movieOverview);
        }

        return rootView;
    }
}
