package com.example.android.popularmoviesapp;

public class TransferData {

    Object savedMoviesList;
    int adapterType;

    final static int ARRAY_ADAPTER_TYPE = 0;
    final static int CURSOR_ADAPTER_TYPE = 1;

    public TransferData(Object savedMoviesList, int adapterType) {
        this.savedMoviesList = savedMoviesList;
        this.adapterType = adapterType;
    }
}