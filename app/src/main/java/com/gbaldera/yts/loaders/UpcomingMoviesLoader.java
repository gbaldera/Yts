package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsUpcomingMovie;
import com.github.underscore._;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.enumerations.Extended2;

import java.util.ArrayList;
import java.util.List;

public class UpcomingMoviesLoader extends BaseMoviesLoader<List<Movie>> {

    public UpcomingMoviesLoader(Context context) {
        super(context);
    }

    @Override
    protected List<Movie> getMoviesFromTrakt(List<String> imbIds) {
        return ServicesHelper.getTrakt(getContext())
                .movieService().summaries(_.join(imbIds, ","), Extended2.FULL);
    }

    @Override
    protected List<YtsMovie> getMoviesFromYts() {
        List<YtsUpcomingMovie> upcomingMoviesList = ServicesHelper.getYtsService(getContext())
                .upcoming();

        List<YtsMovie> movies = new ArrayList<YtsMovie>();

        for (YtsUpcomingMovie upcomingMovie : upcomingMoviesList){
            movies.add(new YtsMovie(upcomingMovie));
        }

        return movies;
    }
}
