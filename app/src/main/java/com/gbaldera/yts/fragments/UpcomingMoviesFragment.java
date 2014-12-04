package com.gbaldera.yts.fragments;


import android.content.Loader;

import com.gbaldera.yts.loaders.UpcomingMoviesLoader;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

public class UpcomingMoviesFragment extends BaseMovieFragment {
    @Override
    protected int getLoaderId() {
        return BaseMovieFragment.UPCOMING_MOVIES_LOADER_ID;
    }

    @Override
    protected Loader<List<Movie>> getLoader() {
        return new UpcomingMoviesLoader(getActivity());
    }
}
