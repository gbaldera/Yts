package com.gbaldera.yts.fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = mMoviesAdapter.getItem(position);
        String imdbId = movie.imdb_id;

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.imdb.com/title/" + imdbId + "/"));

        getActivity().startActivity(intent);
    }

}
