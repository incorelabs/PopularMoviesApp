package com.example.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieCard implements Parcelable {
    String movieTitle;          // The title of the movie.
    String movieReleaseDate;    // The release date of the movie
    String moviePosterUrl;      // The URL to the movie poster
    String movieOverview;       // The Plot Synopsis for the movie
    float movieRating;          // The viewers rating for the movie

    public MovieCard(String movieTitle, String movieReleaseDate, String moviePosterUrl, String movieOverview, float movieRating) {
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.movieOverview = movieOverview;
        this.movieRating = movieRating;
    }

    protected MovieCard(Parcel in) {
        movieTitle = in.readString();
        movieReleaseDate = in.readString();
        moviePosterUrl = in.readString();
        movieOverview = in.readString();
        movieRating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(movieReleaseDate);
        dest.writeString(moviePosterUrl);
        dest.writeString(movieOverview);
        dest.writeFloat(movieRating);
    }

    public static final Creator<MovieCard> CREATOR = new Creator<MovieCard>() {
        @Override
        public MovieCard createFromParcel(Parcel in) {
            return new MovieCard(in);
        }

        @Override
        public MovieCard[] newArray(int size) {
            return new MovieCard[size];
        }
    };
}
