package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieList;
import com.github.underscore._;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.enumerations.Extended2;

import java.util.List;

public class LatestMoviesLoader extends BaseMoviesLoader<List<Movie>> {

    public LatestMoviesLoader(Context context) {
        super(context);
    }

    @Override
    protected List<Movie> getMoviesFromTrakt(List<String> imbIds) {
        return ServicesHelper.getTrakt(getContext())
                .movieService().summaries(_.join(imbIds, ","), Extended2.FULL);
    }

    @Override
    protected List<YtsMovie> getMoviesFromYts() {
        YtsMovieList ytsMovieList = ServicesHelper.getYtsService(getContext()).
                list(30, 1, YtsMovieQuality.ALL, 0, null, YtsMovieGenre.ALL,
                        YtsMovieSort.DATE, YtsMovieOrder.DESC);
        return ytsMovieList.MovieList;
    }
}
