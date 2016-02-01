package com.example.android.popularmoviesapp;

public class MovieCard {
    String movieTitle;          // The title of the movie.
    String movieReleaseDate;    // The release date of the movie
    float movieRating;          // The viewers rating for the movie
    String moviePosterUrl;      // The URL to the movie poster

    public MovieCard(String movieTitle, String movieReleaseDate, float movieRating, String moviePosterUrl) {
        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.movieRating = movieRating;
        this.moviePosterUrl = moviePosterUrl;
    }
}
