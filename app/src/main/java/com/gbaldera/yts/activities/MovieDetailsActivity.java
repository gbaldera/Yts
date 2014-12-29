package com.gbaldera.yts.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.ColorHelper;
import com.gbaldera.yts.helpers.DisplayHelper;
import com.gbaldera.yts.helpers.TextHelper;
import com.gbaldera.yts.helpers.TraktHelper;
import com.gbaldera.yts.loaders.MovieDetailsLoader;
import com.gbaldera.yts.loaders.MovieDetailsYtsLoader;
import com.gbaldera.yts.models.YtsMovieDetailsSummary;
import com.gbaldera.yts.views.ObservableScrollView;
import com.jakewharton.trakt.entities.Movie;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.List;

public class MovieDetailsActivity extends BaseActivity implements
        ObservableScrollView.OnScrollChangedCallback {

    public static final int MOVIE_TRAKT_LOADER_ID = 100;
    public static final int MOVIE_YTS_LOADER_ID = 101;
    public static final int MOVIE_ACTORS_LOADER_ID = 103;
    public static final int RELATED_MOVIES_LOADER_ID = 104;

    public static final String IMDB_ID = "imdbId";
    public static final String YTS_ID = "ytsId";
    private String imdbId;

    private Movie mMovie;
    private List<Movie> mRelatedMovies;
    private YtsMovieDetailsSummary movieSummary;

    private int mActionBarBackgroundColor;
    private int mStatusBarColor;
    private int mLastDampedScroll;

    private ImageView mFanArt, mPoster;
    private View mDetailsArea;
    private TextView mTitle, mPlot, mGenre, mRuntime, mReleaseDate, mRating, mTagline, mCertification;
    private ObservableScrollView mScrollView;
    private View mProgressBar;
    private Activity mActivity;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mActionBarToolbar != null){
                    mActionBarToolbar.setTitle("");
                }
            }
        });

        imdbId = getIntent().getExtras().getString(IMDB_ID);
        if (TextUtils.isEmpty(imdbId)){
            finish();
            return;
        }

        // TODO: get this from poster's color palette
        mActionBarBackgroundColor = getResources().getColor(R.color.color_primary);
        mStatusBarColor = getResources().getColor(R.color.color_primary_dark);

        mDetailsArea = findViewById(R.id.details_area);
        mTitle = (TextView) findViewById(R.id.movie_title);
        mPlot = (TextView) findViewById(R.id.movie_plot);
        mGenre = (TextView) findViewById(R.id.movie_genre);
        mRuntime = (TextView) findViewById(R.id.movie_runtime);
        mReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        mRating = (TextView) findViewById(R.id.movie_rating);
        mTagline = (TextView) findViewById(R.id.movie_tagline);
        mCertification = (TextView) findViewById(R.id.movie_certification);
        mPoster = (ImageView) findViewById(R.id.movie_poster);
        mFanArt = (ImageView)findViewById(R.id.movie_fanart);
        //mActorsLayout = (HorizontalCardLayout) view.findViewById(R.id.horizontal_card_layout);

        mScrollView = (ObservableScrollView) findViewById(R.id.scrollview);
        mScrollView.setOnScrollChangedCallback(this);
        mScrollView.setVisibility(View.GONE);

        mProgressBar = findViewById(R.id.progress_layout);
        mProgressBar.setVisibility(View.VISIBLE);

        mActivity = this;

        // init loaders
        Bundle args = new Bundle();
        args.putString(IMDB_ID, imdbId);
        getLoaderManager().initLoader(MOVIE_YTS_LOADER_ID, args, mMovieYtsLoaderCallbacks);
        getLoaderManager().initLoader(MOVIE_TRAKT_LOADER_ID, args, mMovieTraktLoaderCallbacks);

        onScroll(-1, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        boolean haveTrailer = movieSummary != null && !TextUtils.isEmpty(movieSummary.YoutubeTrailerUrl);
        menu.findItem(R.id.action_trailer).setVisible(haveTrailer);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_trailer:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(movieSummary.YoutubeTrailerUrl));
                startActivity(intent);
                return true;
            case R.id.action_yts:
                try{
                    String encondedTitle = URLEncoder.encode(mMovie.title, "utf-8");
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://yts.re/browse-movie/" + encondedTitle + "/All/All/0/latest"));
                    startActivity(intent);
                }
                catch (UnsupportedEncodingException e){}
                return true;
            case R.id.action_imdb:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.imdb.com/title/" + imdbId + "/"));
                startActivity(intent);
                return true;
            case R.id.action_subtitles:
                intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.yifysubtitles.com/movie-imdb/" + imdbId));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onScroll(int l, int scrollPosition) {
        int headerHeight = mFanArt.getHeight() - mActionBarToolbar.getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateActionBarTransparency(ratio);
        setStatusBarColor(ratio, mStatusBarColor);
        updateParallaxEffect(scrollPosition);
    }

    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        mActionBarToolbar.setBackgroundColor(ColorHelper.modifyAlpha(mActionBarBackgroundColor, newAlpha));
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = mLastDampedScroll - dampedScroll;
        mFanArt.offsetTopAndBottom(-offset);

        mLastDampedScroll = dampedScroll;
    }

    private void populateMovieViews() {
        mTitle.setText(mMovie.title);
        //mTitle.setTypeface(mCondensedRegular);

        mPlot.setText(mMovie.overview);

        if (mMovie.tagline.isEmpty())
            mTagline.setVisibility(TextView.GONE);
        else
            mTagline.setText(mMovie.tagline);

        if (movieSummary != null && !TextUtils.isEmpty(movieSummary.Genre1))
            mGenre.setText(movieSummary.Genre1 + (!TextUtils.isEmpty(movieSummary.Genre2) ? ", " + movieSummary.Genre2 : ""));
        else
            mGenre.setVisibility(View.GONE);


        mRuntime.setText(TextHelper.getPrettyRuntime(this, mMovie.runtime));
        mReleaseDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(mMovie.released));

        if(mMovie.ratings.percentage == 0)
            mRating.setText(R.string.stringNA);
        else
            mRating.setText(mMovie.ratings.percentage.toString() + "%");

        if (!TextUtils.isEmpty(mMovie.certification)) {
            mCertification.setText(mMovie.certification);
        } else {
            mCertification.setText(R.string.stringNA);
        }

        // load images
        int poster_size;
        if (DisplayHelper.isVeryHighDensityScreen(this)) {
            poster_size = TraktHelper.POSTER_SIZE_SPEC_138;
        } else {
            poster_size = TraktHelper.POSTER_SIZE_SPEC_300;
        }

        String poster = TraktHelper.resizeImage(mMovie.images.poster, poster_size);
        String fan_art = TraktHelper.resizeImage(mMovie.images.fanart, TraktHelper.FAN_ART_SIZE_SPEC_940);
        Picasso.with(this)
                .load(poster)
                .into(mPoster);
        Picasso.with(this)
                .load(fan_art)
                .into(mFanArt);

    }

    private LoaderManager.LoaderCallbacks<Movie> mMovieTraktLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<Movie>() {
        @Override
        public Loader<Movie> onCreateLoader(int loaderId, Bundle args) {
            return new MovieDetailsLoader(mActivity, args.getString(IMDB_ID));
        }

        @Override
        public void onLoadFinished(Loader<Movie> movieLoader, Movie movie) {
            mMovie = movie;
            mScrollView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            if (movie != null) {
                populateMovieViews();
                invalidateOptionsMenu();
            } else {
                // display offline message
                mPlot.setText(R.string.offline);
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie> movieLoader) {
            // nothing to do
        }
    };

    private LoaderManager.LoaderCallbacks<YtsMovieDetailsSummary> mMovieYtsLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<YtsMovieDetailsSummary>() {
        @Override
        public Loader<YtsMovieDetailsSummary> onCreateLoader(int id, Bundle args) {
            return new MovieDetailsYtsLoader(mActivity, args.getString(IMDB_ID));
        }

        @Override
        public void onLoadFinished(Loader<YtsMovieDetailsSummary> loader,
                                   YtsMovieDetailsSummary data) {
            movieSummary = data;
        }

        @Override
        public void onLoaderReset(Loader<YtsMovieDetailsSummary> loader) {
        }
    };
}
