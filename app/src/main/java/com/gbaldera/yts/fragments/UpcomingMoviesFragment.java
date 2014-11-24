package com.gbaldera.yts.fragments;


import android.content.Loader;

import com.gbaldera.yts.loaders.PopularMoviesLoader;
import com.gbaldera.yts.models.Movie;

import java.util.List;

public class UpcomingMoviesFragment extends BaseMovieFragment {
    @Override
    protected int getLoaderId() {
        return BaseMovieFragment.UPCOMING_MOVIES_LOADER_ID;
    }

    @Override
    protected Loader<List<Movie>> getLoader() {
        return new PopularMoviesLoader(getActivity());
    }
}
