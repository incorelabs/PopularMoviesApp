package com.example.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewCard implements Parcelable {

    String reviewAuthor;
    String reviewContent;

    public ReviewCard(String reviewAuthor, String reviewContent) {
        this.reviewAuthor = reviewAuthor;
        this.reviewContent = reviewContent;
    }

    protected ReviewCard(Parcel in) {
        this.reviewAuthor = in.readString();
        this.reviewContent = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reviewAuthor);
        dest.writeString(this.reviewContent);
    }

    public static final Creator<ReviewCard> CREATOR = new Creator<ReviewCard>() {
        @Override
        public ReviewCard createFromParcel(Parcel in) {
            return new ReviewCard(in);
        }

        @Override
        public ReviewCard[] newArray(int size) {
            return new ReviewCard[size];
        }
    };
}
