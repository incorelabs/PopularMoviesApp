package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract.FavouriteMoviesEntry;
import com.squareup.picasso.Picasso;

public class FavouriteMovieCursorAdapter extends CursorAdapter {

    public FavouriteMovieCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRating;
        public final TextView movieReleaseYear;
        public final ImageView moviePoster;

        public ViewHolder(View view) {
            movieTitle = (TextView) view.findViewById(R.id.list_movie_title);
            movieRating = (TextView) view.findViewById(R.id.list_movie_rating);
            movieReleaseYear = (TextView) view.findViewById(R.id.list_movie_year);
            moviePoster = (ImageView) view.findViewById(R.id.list_movie_poster);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String movieTitle = cursor.getString(
                cursor.getColumnIndex(FavouriteMoviesEntry.COLUMN_TITLE)
        );

        viewHolder.movieTitle.setText(movieTitle);

        String moviePoster = cursor.getString(
                cursor.getColumnIndex(FavouriteMoviesEntry.COLUMN_POSTER)
        );

        Picasso.with(context)
                .load(moviePoster)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .into(viewHolder.moviePoster);

        String movieReleaseYear = cursor.getString(
                cursor.getColumnIndex(FavouriteMoviesEntry.COLUMN_RELEASE_DATE)
        ).split("-")[0];

        viewHolder.movieReleaseYear.setText(movieReleaseYear);

        float movieRating = cursor.getFloat(
                cursor.getColumnIndex(FavouriteMoviesEntry.COLUMN_USER_RATING)
        );

        viewHolder.movieRating.setText(Float.toString(movieRating));
    }
}
