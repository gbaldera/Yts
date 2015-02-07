package com.gbaldera.yts.fragments;


import android.content.Loader;

import com.gbaldera.yts.loaders.PopularMoviesLoader;
import com.gbaldera.yts.models.YtsMovie;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

public class PopularMoviesFragment extends BaseMovieFragment {
    @Override
    protected int getLoaderId() {
        return BaseMovieFragment.POPULAR_MOVIES_LOADER_ID;
    }

    @Override
    protected Loader<List<YtsMovie>> getLoader() {
        return new PopularMoviesLoader(getActivity());
    }
}
