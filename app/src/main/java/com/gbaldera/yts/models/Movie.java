package com.gbaldera.yts.models;


import android.content.Context;
import android.text.TextUtils;

import com.gbaldera.yts.helpers.DisplayHelper;
import com.gbaldera.yts.helpers.TmdbHelper;

public class Movie {
    public YtsMovie ytsMovie;
    public com.uwetrottmann.tmdb.entities.Movie tmdbMovie;

    public Movie(){}

    public Movie(YtsMovie ytsMovie, com.uwetrottmann.tmdb.entities.Movie tmdbMovie){
        this.ytsMovie = ytsMovie;
        this.tmdbMovie = tmdbMovie;
    }

    public String getPosterImageUrl(Context context){

        String mPosterBaseImageUrl;

        // figure out which size of posters to load based on screen density
        if (DisplayHelper.isVeryHighDensityScreen(context)) {
            mPosterBaseImageUrl = TmdbHelper.DEFAULT_BASE_URL + TmdbHelper.POSTER_SIZE_SPEC_W342;
        } else {
            mPosterBaseImageUrl = TmdbHelper.DEFAULT_BASE_URL + TmdbHelper.POSTER_SIZE_SPEC_W154;
        }

        return !TextUtils.isEmpty(tmdbMovie.poster_path) ?
                mPosterBaseImageUrl + tmdbMovie.poster_path : null;
    }
}
