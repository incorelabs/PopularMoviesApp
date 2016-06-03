package com.example.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieCard implements Parcelable {

    int movieId;                // The online id for the movie
    byte isFavourite;           // Tells if the movie is a favourite movie
    String movieTitle;          // The title of the movie
    String movieReleaseDate;    // The release date of the movie
    String moviePosterUrl;      // The URL to the movie poster
    String movieOverview;       // The Plot Synopsis for the movie
    float movieRating;          // The viewers rating for the movie

    public MovieCard(int movieId, byte isFavourite, String movieTitle, String movieReleaseDate, String moviePosterUrl, String movieOverview, float movieRating) {
        this.movieId = movieId;
        this.isFavourite = isFavourite;
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePosterUrl = moviePosterUrl;
        this.movieOverview = movieOverview;
        this.movieRating = movieRating;
    }

    protected MovieCard(Parcel in) {
        this.movieId = in.readInt();
        this.isFavourite = in.readByte();
        this.movieTitle = in.readString();
        this.movieReleaseDate = in.readString();
        this.moviePosterUrl = in.readString();
        this.movieOverview = in.readString();
        this.movieRating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieId);
        dest.writeByte(this.isFavourite);
        dest.writeString(this.movieTitle);
        dest.writeString(this.movieReleaseDate);
        dest.writeString(this.moviePosterUrl);
        dest.writeString(this.movieOverview);
        dest.writeFloat(this.movieRating);
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
