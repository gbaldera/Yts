package com.gbaldera.yts.activities;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.gbaldera.yts.BuildConfig;
import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.ColorHelper;
import com.gbaldera.yts.widgets.MultiSwipeRefreshLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public abstract class BaseActivity extends AppCompatActivity implements
        MultiSwipeRefreshLayout.CanChildScrollUpCallback {

    protected Toolbar mActionBarToolbar;
    protected DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout mAdViewContainer;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        trySetupSwipeRefresh();
        trySetupAdView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        setupActionBar();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

    protected void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void trySetupSwipeRefresh() {
        //mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.refresh_progress_1,
                    R.color.refresh_progress_2,
                    R.color.refresh_progress_3);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });

            if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
                mswrl.setCanChildScrollUpCallback(this);
            }
        }
    }

    protected void trySetupAdView(){

        mAdViewContainer = (LinearLayout) findViewById(R.id.admob_container);

        if(mAdViewContainer != null){
            mAdView = new AdView(this);
            mAdView.setAdUnitId(BuildConfig.AD_UNIT_ID);
            mAdView.setAdSize(AdSize.BANNER);
            mAdViewContainer.addView(mAdView);

            AdRequest.Builder builder = new AdRequest.Builder();
            if(BuildConfig.DEBUG){
                builder.addTestDevice(BuildConfig.AD_TEST_DEVICE_ID);
            }
            AdRequest adRequest = builder.build();
            mAdView.loadAd(adRequest);
        }
    }

    protected void requestDataRefresh(){}

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        return false;
    }

    public void setStatusBarColor(float scroll, int color) {
        final int statusBarColor = ColorHelper.blendColors(color, 0, scroll);
        if (mDrawerLayout != null) {
            mDrawerLayout.setStatusBarBackgroundColor(statusBarColor);
        } else if (Build.VERSION.SDK_INT >= 21) {
            this.getWindow().setStatusBarColor(statusBarColor);
        }
    }
}
