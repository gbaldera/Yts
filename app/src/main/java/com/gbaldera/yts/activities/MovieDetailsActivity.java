package com.gbaldera.yts.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.ColorHelper;
import com.gbaldera.yts.views.ObservableScrollView;

public class MovieDetailsActivity extends BaseActivity implements
        ObservableScrollView.OnScrollChangedCallback {

    public static final String IMDB_ID = "imdbId";
    public static final String YTS_ID = "ytsId";
    private String imdbId;

    private int mActionBarBackgroundColor;
    private int mStatusBarColor;
    private int mLastDampedScroll;

    private ImageView mFanArt, mPoster;
    private View mDetailsArea;
    private TextView mTitle, mPlot, mGenre, mRuntime, mReleaseDate, mRating, mTagline, mCertification;
    private ObservableScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

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

        onScroll(-1, 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void populateMovieViews() {}
}
