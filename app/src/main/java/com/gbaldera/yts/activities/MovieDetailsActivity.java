package com.gbaldera.yts.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbaldera.yts.PaletteTransformation;
import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.ColorHelper;
import com.gbaldera.yts.helpers.TextHelper;
import com.gbaldera.yts.helpers.TraktHelper;
import com.gbaldera.yts.loaders.MovieDetailsLoader;
import com.gbaldera.yts.loaders.MovieDetailsYtsLoader;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieDetailsSummary;
import com.gbaldera.yts.views.ObservableScrollView;

import com.jakewharton.trakt.entities.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.androidutils.AndroidUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @InjectView(R.id.movie_fanart) ImageView mFanArt;
    @InjectView(R.id.movie_poster) ImageView mPoster;
    @InjectView(R.id.details_area) View mDetailsArea;
    @InjectView(R.id.movie_title) TextView mTitle;
    @InjectView(R.id.movie_plot) TextView mPlot;
    @InjectView(R.id.movie_genre) TextView mGenre;
    @InjectView(R.id.movie_runtime) TextView mRuntime;
    @InjectView(R.id.movie_release_date) TextView mReleaseDate;
    @InjectView(R.id.movie_rating) TextView mRating;
    @InjectView(R.id.movie_tagline) TextView mTagline;
    @InjectView(R.id.movie_certification) TextView mCertification;
    @InjectView(R.id.scrollview) ObservableScrollView mScrollView;
    @InjectView(R.id.progress_layout) View mProgressBar;
    private Activity mActivity;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.inject(this);

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

        mActionBarBackgroundColor = getResources().getColor(R.color.color_primary);
        mStatusBarColor = getResources().getColor(R.color.color_primary_dark);

        mScrollView.setOnScrollChangedCallback(this);
        mScrollView.setVisibility(View.GONE);

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

        boolean haveTrailer = (mMovie != null && !TextUtils.isEmpty(mMovie.trailer))
                || movieSummary != null && !TextUtils.isEmpty(movieSummary.YoutubeTrailerUrl);
        boolean haveTorrent = movieSummary != null && !TextUtils.isEmpty(movieSummary.YoutubeTrailerUrl);
        boolean haveShare = movieSummary != null && mMovie != null;

        menu.findItem(R.id.action_trailer).setVisible(haveTrailer);
        menu.findItem(R.id.action_torrent).setVisible(haveTorrent);
        menu.findItem(R.id.action_share).setVisible(haveShare);

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
                String trailer = "";

                if(mMovie != null && !TextUtils.isEmpty(mMovie.trailer)){
                    trailer = mMovie.trailer;
                }
                else if(movieSummary != null && !TextUtils.isEmpty(movieSummary.YoutubeTrailerUrl)){
                    trailer = movieSummary.YoutubeTrailerUrl;
                }

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer));
                startActivity(intent);
                return true;
            case R.id.action_torrent:
                showTorrentDialogChooser();
                return true;
            case R.id.action_share:
                try {
                    String encondedTitle = URLEncoder.encode(mMovie.title, "utf-8");
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, movieSummary.MovieTitle +
                            " - " + "https://yts.re/browse-movie/" + encondedTitle + "/All/All/0/latest");
                    startActivity(intent);
                }catch (UnsupportedEncodingException e){}
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

    private void showTorrentDialogChooser(){
        if(movieSummary != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_torrent).setItems(movieSummary.MovieAvailableQualities
                            .toArray(new CharSequence[movieSummary.MovieAvailableQualities.size()]),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            YtsMovie ytsMovie = movieSummary.MovieList.get(which);
                            Intent torrentIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(ytsMovie.TorrentUrl));
                            startActivity(torrentIntent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void populateMovieGenre(){
        if (movieSummary != null && !TextUtils.isEmpty(movieSummary.Genre1))
            mGenre.setText(movieSummary.Genre1 + (!TextUtils.isEmpty(movieSummary.Genre2) ? ", " + movieSummary.Genre2 : ""));
        else
            mGenre.setVisibility(View.GONE);
    }

    private void populateMovieViews() {
        mTitle.setText(mMovie.title);
        //mTitle.setTypeface(mCondensedRegular);

        mPlot.setText(mMovie.overview);

        if (mMovie.tagline.isEmpty())
            mTagline.setVisibility(TextView.GONE);
        else
            mTagline.setText(mMovie.tagline);

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
        String poster = TraktHelper.resizeImage(mMovie, TraktHelper.TraktImageType.POSTER,
                TraktHelper.TraktImageSize.THUMB);
        String fan_art = TraktHelper.resizeImage(mMovie, TraktHelper.TraktImageType.FANART,
                TraktHelper.TraktImageSize.THUMB);
        Picasso.with(this)
                .load(poster)
                .transform(PaletteTransformation.instance())
                .into(mPoster, new Callback.EmptyCallback() {
                    @Override public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) mPoster.getDrawable()).getBitmap();
                        Palette palette = PaletteTransformation.getPalette(bitmap);

                        Palette.Swatch primary = palette.getVibrantSwatch();
                        Palette.Swatch secondary = palette.getDarkVibrantSwatch();

                        if (primary == null) {
                            primary = palette.getMutedSwatch();
                        }
                        if (secondary == null) {
                            secondary = palette.getDarkMutedSwatch();
                        }

                        if(primary != null && secondary != null){
                            mActionBarBackgroundColor = primary.getRgb();
                            mStatusBarColor = secondary.getRgb();
                        }

                        // change the background color of the details view
                        try {
                            ObjectAnimator backgroundColorAnimator = ObjectAnimator.
                                    ofObject(mDetailsArea, "backgroundColor", new ArgbEvaluator(),
                                            0xFF666666, mActionBarBackgroundColor);
                            backgroundColorAnimator.setDuration(500);
                            backgroundColorAnimator.start();
                        } catch (Exception e) {
                            mDetailsArea.setBackgroundColor(mActionBarBackgroundColor);
                        }
                    }
                });
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
                if (AndroidUtils.isNetworkConnected(mActivity)) {
                    mPlot.setText(R.string.movies_details_empty);
                } else {
                    mPlot.setText(R.string.offline);
                }
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
            populateMovieGenre();
            invalidateOptionsMenu();
        }

        @Override
        public void onLoaderReset(Loader<YtsMovieDetailsSummary> loader) {
        }
    };
}
