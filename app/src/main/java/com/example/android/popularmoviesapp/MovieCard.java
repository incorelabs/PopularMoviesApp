package com.example.android.popularmoviesapp;

public class MovieCard {
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
}
