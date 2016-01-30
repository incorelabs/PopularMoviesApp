package com.example.android.popularmoviesapp;

public class MovieCard {
    String movieTitle;  // The title of the movie.
    String movieReleaseDate;    // The release date of the movie
    float movieRating;

    public MovieCard(String movieTitle, String movieReleaseDate, float movieRating) {
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieRating = movieRating;
    }
}
