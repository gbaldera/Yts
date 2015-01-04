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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gbaldera.yts.R;
import com.gbaldera.yts.activities.MovieDetailsActivity;
import com.gbaldera.yts.adapters.SearchMoviesAdapter;
import com.gbaldera.yts.loaders.SearchMoviesLoader;
import com.jakewharton.trakt.entities.Movie;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchMoviesFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int SEARCH_MOVIES_LOADER_ID = 100;
    public static final String SEARCH_QUERY = "query";
    public static final String PROGRESS_RUNNING = "progressRunning";

    private String mQuery = "";
    private SearchMoviesAdapter mMoviesAdapter;
    private boolean progressRunning;

    @InjectView(R.id.search_results_container) View mResultsContainer;
    @InjectView(R.id.listView) ListView mListView;
    @InjectView(R.id.progress_bar) ProgressBar mProgressBar;
    @InjectView(R.id.no_results) TextView noResults;

    public SearchMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMoviesAdapter = new SearchMoviesAdapter(getActivity());
        mListView.setAdapter(mMoviesAdapter);

        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, mQuery);
        getLoaderManager().initLoader(SEARCH_MOVIES_LOADER_ID, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search_movies, container, false);
        ButterKnife.inject(this, v);

        mListView.setEmptyView(noResults);
        mListView.setOnItemClickListener(this);

        if(savedInstanceState != null){
            setProgressVisible(savedInstanceState.getBoolean(PROGRESS_RUNNING));
        }
        else {
            setProgressVisible(false);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof SearchMoviesFragmentListener) {
            ((SearchMoviesFragmentListener) getActivity()).onFragmentViewCreated(this);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getActivity() instanceof SearchMoviesFragmentListener) {
            ((SearchMoviesFragmentListener) getActivity()).onFragmentAttached(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof SearchMoviesFragmentListener) {
            ((SearchMoviesFragmentListener) getActivity()).onFragmentDetached(this);
        }
    }

    public void updateSearch(String query){
        mQuery = query;
        Bundle args = new Bundle();
        args.putString(SEARCH_QUERY, mQuery);
        getLoaderManager().restartLoader(SEARCH_MOVIES_LOADER_ID, args, this);
    }

    public void clearSearch(){
        mQuery = "";
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

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        setProgressVisible(true);
        return new SearchMoviesLoader(getActivity(), args.getString(SEARCH_QUERY));
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mMoviesAdapter.setData(data);
        setProgressVisible(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mMoviesAdapter.setData(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(PROGRESS_RUNNING, progressRunning);
    }

    private void setProgressVisible(boolean visible) {
        progressRunning = visible;
        mResultsContainer.setVisibility(visible ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

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
    public interface SearchMoviesFragmentListener {
        public void onFragmentViewCreated(SearchMoviesFragment fragment);
        public void onFragmentAttached(SearchMoviesFragment fragment);
        public void onFragmentDetached(SearchMoviesFragment fragment);
    }

}
