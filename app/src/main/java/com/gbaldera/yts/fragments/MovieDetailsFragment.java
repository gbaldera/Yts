package com.gbaldera.yts.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gbaldera.yts.R;
import com.gbaldera.yts.activities.MovieDetailsActivity;

public class MovieDetailsFragment extends Fragment {

    private String imdbId;

    public static MovieDetailsFragment newInstance(String imdbId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(MovieDetailsActivity.IMDB_ID, imdbId);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imdbId = getArguments().getString(MovieDetailsActivity.IMDB_ID);
        if (TextUtils.isEmpty(imdbId)) {
            getFragmentManager().popBackStack();
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_details, container, false);
        return v;
    }


}
