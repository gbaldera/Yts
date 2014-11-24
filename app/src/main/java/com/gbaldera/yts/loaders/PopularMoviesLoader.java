package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.models.Movie;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.network.YtsClient;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.ArrayList;
import java.util.List;

public class PopularMoviesLoader extends GenericSimpleLoader<List<Movie>> {



    public PopularMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Movie> loadInBackground() {

        List<Movie> movies = new ArrayList<Movie>();

        try{
            YtsMovieList ytsMovieList = YtsClient.create().
                    listMovies(20, 1, "ALL", 0, null, "ALL", "downloaded", "desc");
            for(YtsMovie ytsMovie: ytsMovieList.MovieList){
                movies.add(new Movie(ytsMovie, new com.uwetrottmann.tmdb.entities.Movie()));
            }

            return movies;
        }
        catch (Exception e){}

        return null;
    }

    protected List<YtsMovie> getMoviesFromYts() {
        return null;
    }
}
