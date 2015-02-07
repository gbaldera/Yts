package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieDetails;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import timber.log.Timber;

public class MovieDetailsYtsLoader extends GenericSimpleLoader<YtsMovie> {
    private int movieId;

    public MovieDetailsYtsLoader(Context context, int id) {
        super(context);
        this.movieId = id;
    }

    @Override
    public YtsMovie loadInBackground() {
        try{
            YtsMovieDetails details = ServicesHelper.getYtsService(getContext()).
                    movie_details(movieId, true, false);
            return details.data;
        }
        catch (Exception e){
            Timber.e(e, "Downloading movie details from Yts failed");
        }
        return null;
    }
}
