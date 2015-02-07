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
import com.gbaldera.yts.loaders.MovieDetailsLoader;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsTorrent;
import com.gbaldera.yts.views.ObservableScrollView;
import com.github.underscore._;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.androidutils.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MovieDetailsActivity extends BaseActivity implements
        ObservableScrollView.OnScrollChangedCallback {

    public static final int MOVIE_DETAILS_LOADER_ID = 100;
    public static final int MOVIE_ACTORS_LOADER_ID = 103;
    public static final int RELATED_MOVIES_LOADER_ID = 104;

    public static final String IMDB_ID = "imdbId";
    public static final String YTS_ID = "ytsId";
    private String imdbId;
    private int ytsId;

    private YtsMovie mMovie;
    private List<YtsMovie> mRelatedMovies;
    private ArrayList<String> movieAvailableQualities = new ArrayList<>();

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

        ytsId = getIntent().getExtras().getInt(YTS_ID);
        if (ytsId == 0){
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
        args.putInt(YTS_ID, ytsId);
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, args, mMovieDetailsLoaderCallbacks);

        onScroll(-1, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        boolean haveTrailer = mMovie != null && !TextUtils.isEmpty(mMovie.yt_trailer_code);
        boolean haveTorrent = mMovie != null && mMovie.torrents.size() > 0;
        boolean haveShare = mMovie != null && !TextUtils.isEmpty(mMovie.url);

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
                if(mMovie != null && !TextUtils.isEmpty(mMovie.yt_trailer_code)){
                    String trailer = "https://www.youtube.com/watch?v=" + mMovie.yt_trailer_code;
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer));
                    startActivity(intent);
                }
                return true;
            case R.id.action_torrent:
                showTorrentDialogChooser();
                return true;
            case R.id.action_share:
                if(mMovie != null && !TextUtils.isEmpty(mMovie.url)){
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, mMovie.title_long +
                            " - " + mMovie.url);
                    startActivity(intent);
                }
                return true;
            case R.id.action_yts:
                if(mMovie != null && !TextUtils.isEmpty(mMovie.url)){
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(mMovie.url));
                    startActivity(intent);
                }
                return true;
            case R.id.action_imdb:
                if(mMovie != null){
                    intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.imdb.com/title/" + mMovie.imdb_code + "/"));
                    startActivity(intent);
                }
                return true;
            case R.id.action_subtitles:
                if(mMovie != null){
                    intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.yifysubtitles.com/movie-imdb/" + mMovie.imdb_code));
                    startActivity(intent);
                }
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
        if(mMovie != null && movieAvailableQualities.size() > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_torrent).setItems(movieAvailableQualities
                            .toArray(new CharSequence[movieAvailableQualities.size()]),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            YtsTorrent torrent = mMovie.torrents.get(which);
                            Intent torrentIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(torrent.url));
                            startActivity(torrentIntent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void populateMovieViews() {
        mTitle.setText(mMovie.title);
        //mTitle.setTypeface(mCondensedRegular);

        mPlot.setText(mMovie.description_full);

        mTagline.setVisibility(TextView.GONE);

        mRuntime.setText(TextHelper.getPrettyRuntime(this, mMovie.runtime));
        mReleaseDate.setText(String.valueOf(mMovie.year));

        if (mMovie.genres.size() > 0)
            mGenre.setText(TextUtils.join(", ", mMovie.genres));
        else
            mGenre.setVisibility(View.GONE);

        if(mMovie.rating == 0)
            mRating.setText(R.string.stringNA);
        else{
            mRating.setText(mMovie.rating.toString());
        }

        if (!TextUtils.isEmpty(mMovie.mpa_rating)) {
            mCertification.setText(mMovie.mpa_rating);
        } else {
            mCertification.setText(R.string.stringNA);
        }

        // load images
        String poster = mMovie.images.medium_cover_image;
        String fan_art = mMovie.images.background_image;

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

    private LoaderManager.LoaderCallbacks<YtsMovie> mMovieDetailsLoaderCallbacks
            = new LoaderManager.LoaderCallbacks<YtsMovie>() {
        @Override
        public Loader<YtsMovie> onCreateLoader(int loaderId, Bundle args) {
            return new MovieDetailsLoader(mActivity, args.getInt(YTS_ID));
        }

        @Override
        public void onLoadFinished(Loader<YtsMovie> movieLoader, YtsMovie movie) {
            mMovie = movie;
            mScrollView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);

            if (movie != null) {
                try {
                    List<Object> qualities = _.pluck(movie.torrents, "quality");
                    for(Object object : qualities){
                        if (object == null)
                            continue;
                        movieAvailableQualities.add(object.toString());
                    }
                } catch (NoSuchFieldException e) {
                }
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
        public void onLoaderReset(Loader<YtsMovie> movieLoader) {
            // nothing to do
        }
    };
}
