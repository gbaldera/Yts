package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.jakewharton.trakt.entities.Movie;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import timber.log.Timber;

public class MovieLoader extends GenericSimpleLoader<Movie> {
    private String imdbId;

    public MovieLoader(Context context, String imdbId) {
        super(context);
        this.imdbId = imdbId;
    }

    @Override
    public Movie loadInBackground() {
        try {
            return ServicesHelper.getTrakt(getContext())
                    .movieService().summary(imdbId);
        }
        catch (Exception e){
            Timber.e(e, "Downloading movie details failed");
        }

        return null;
    }
}
