package com.example.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class TrailerCard implements Parcelable {

    String trailerTitle;
    String trailerKey;

    public TrailerCard(String trailerTitle, String trailerKey) {
        this.trailerTitle = trailerTitle;
        this.trailerKey = trailerKey;
    }

    protected TrailerCard(Parcel in) {
        this.trailerTitle = in.readString();
        this.trailerKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trailerTitle);
        dest.writeString(this.trailerKey);
    }

    public static final Creator<TrailerCard> CREATOR = new Creator<TrailerCard>() {
        @Override
        public TrailerCard createFromParcel(Parcel in) {
            return new TrailerCard(in);
        }

        @Override
        public TrailerCard[] newArray(int size) {
            return new TrailerCard[size];
        }
    };
}
