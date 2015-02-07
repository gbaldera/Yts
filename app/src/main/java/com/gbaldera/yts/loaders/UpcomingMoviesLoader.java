package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieListUpcoming;
import com.github.underscore._;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.List;

import timber.log.Timber;

public class UpcomingMoviesLoader extends GenericSimpleLoader<List<YtsMovie>> {

    public UpcomingMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<YtsMovie> loadInBackground() {
        try{
            YtsMovieListUpcoming listUpcoming = ServicesHelper.getYtsService(getContext()).
                    list_upcoming();
            return listUpcoming.data.upcoming_movies;
        }
        catch (Exception e){
            Timber.e(e, "Downloading movies failed");
        }

        return null;
    }
}
