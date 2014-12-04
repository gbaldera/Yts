package com.gbaldera.yts.models;


import android.content.Context;
import android.text.TextUtils;

import com.gbaldera.yts.helpers.DisplayHelper;
import com.gbaldera.yts.helpers.TmdbHelper;
import com.gbaldera.yts.helpers.TraktHelper;

public class Movie {
    public YtsMovie ytsMovie;
    public com.jakewharton.trakt.entities.Movie traktMovie;

    public Movie(){}

    public Movie(YtsMovie ytsMovie, com.jakewharton.trakt.entities.Movie traktMovie){
        this.ytsMovie = ytsMovie;
        this.traktMovie = traktMovie;
    }

    public String getPosterImageUrl(Context context){

        int size;

        // figure out which size of posters to load based on screen density
        if (DisplayHelper.isVeryHighDensityScreen(context)) {
            size = TraktHelper.POSTER_SIZE_SPEC_138;
        } else {
            size = TraktHelper.POSTER_SIZE_SPEC_300;
        }

        return traktMovie.images != null && !TextUtils.isEmpty(traktMovie.images.poster) ?
                TraktHelper.resizeImage(traktMovie.images.poster, size) : null;
    }
}
