package com.gbaldera.yts.fragments;


import android.content.Loader;

import com.gbaldera.yts.loaders.LatestMoviesLoader;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

public class LatestMoviesFragment extends BaseMovieFragment {
    @Override
    protected int getLoaderId() {
        return BaseMovieFragment.LATEST_MOVIES_LOADER_ID;
    }

    @Override
    protected Loader<List<Movie>> getLoader() {
        return new LatestMoviesLoader(getActivity());
    }
}
