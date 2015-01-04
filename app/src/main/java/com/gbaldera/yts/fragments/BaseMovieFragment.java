package com.gbaldera.yts.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gbaldera.yts.R;
import com.gbaldera.yts.activities.MovieDetailsActivity;
import com.gbaldera.yts.adapters.MoviesAdapter;
import com.jakewharton.trakt.entities.Movie;
import com.uwetrottmann.androidutils.AndroidUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public abstract class BaseMovieFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    protected static final int LAYOUT = R.layout.fragment_movies;
    @InjectView(R.id.movies_container) View mMoviesContainer;
    @InjectView(R.id.movies_list) GridView mGridView;
    @InjectView(R.id.text_empty) TextView mEmptyView;
    @InjectView(R.id.progress_bar) ProgressBar mProgressBar;

    protected MoviesAdapter mMoviesAdapter;

    public static final int LATEST_MOVIES_LOADER_ID = 100;
    public static final int POPULAR_MOVIES_LOADER_ID = 101;
    public static final int UPCOMING_MOVIES_LOADER_ID = 102;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(LAYOUT, container, false);
        ButterKnife.inject(this, v);

        mGridView.setEmptyView(mEmptyView);
        mGridView.setOnItemClickListener(this);

        setProgressVisible(false, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof MoviesFragmentListener) {
            ((MoviesFragmentListener) getActivity()).onFragmentViewCreated(this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMoviesAdapter = new MoviesAdapter(getActivity());
        mGridView.setAdapter(mMoviesAdapter);

        getLoaderManager().initLoader(getLoaderId(), null, this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof MoviesFragmentListener) {
            ((MoviesFragmentListener) getActivity()).onFragmentAttached(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof MoviesFragmentListener) {
            ((MoviesFragmentListener) getActivity()).onFragmentDetached(this);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int loaderId, Bundle args) {
        setProgressVisible(true, false);
        return getLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (AndroidUtils.isNetworkConnected(getActivity())) {
            mEmptyView.setText(R.string.movies_empty);
        } else {
            mEmptyView.setText(R.string.offline);
        }
        mMoviesAdapter.setData(data);
        setProgressVisible(false, true);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMoviesAdapter.setData(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = mMoviesAdapter.getItem(position);
        String imdbId = movie.imdb_id;

        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.IMDB_ID, imdbId);

        getActivity().startActivity(intent);
    }

    private void setProgressVisible(boolean visible, boolean animate) {
        mMoviesContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public View getGridView(){
        return mGridView;
    }

    public void refreshMovieData(){
        getLoaderManager().restartLoader(getLoaderId(), null, this);
    }

    protected abstract int getLoaderId();
    protected abstract Loader<List<Movie>> getLoader();

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface MoviesFragmentListener {
        public void onFragmentViewCreated(BaseMovieFragment fragment);
        public void onFragmentAttached(BaseMovieFragment fragment);
        public void onFragmentDetached(BaseMovieFragment fragment);
    }

}
