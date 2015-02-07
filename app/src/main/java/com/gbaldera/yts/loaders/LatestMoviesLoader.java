package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieList;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.List;

import timber.log.Timber;

public class LatestMoviesLoader extends GenericSimpleLoader<List<YtsMovie>> {

    public LatestMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<YtsMovie> loadInBackground() {
        try{
            YtsMovieList movieList = ServicesHelper.getYtsService(getContext()).
                    list_movies(30, 1, YtsMovieQuality.ALL, 0, null, YtsMovieGenre.ALL,
                            YtsMovieSort.DATE, YtsMovieOrder.DESC, false);
            return movieList.data.movies;
        }
        catch (Exception e){
            Timber.e(e, "Downloading movies failed");
        }

        return null;
    }
}

