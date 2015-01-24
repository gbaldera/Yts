package com.gbaldera.yts.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.gbaldera.yts.R;
import com.gbaldera.yts.fragments.BaseMovieFragment;
import com.gbaldera.yts.fragments.LatestMoviesFragment;
import com.gbaldera.yts.fragments.PopularMoviesFragment;
import com.gbaldera.yts.fragments.UpcomingMoviesFragment;
import com.gbaldera.yts.widgets.SlidingTabLayout;
import com.rampo.updatechecker.UpdateChecker;

import java.util.HashSet;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseDrawerActivity implements
        BaseMovieFragment.MoviesFragmentListener {

    @InjectView(R.id.view_pager)
    ViewPager mViewPager;

    @InjectView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;
    MainTabsViewPagerAdapter mViewPagerAdapter = null;

    private Set<BaseMovieFragment> mMoviesFragments = new HashSet<BaseMovieFragment>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mViewPagerAdapter = new MainTabsViewPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        // add tabs
        mSlidingTabLayout.setContentDescription(0, getString(R.string.tab_latest));
        mSlidingTabLayout.setContentDescription(0, getString(R.string.tab_popular));
        mSlidingTabLayout.setContentDescription(0, getString(R.string.tab_upcoming));

        Resources res = getResources();
        mSlidingTabLayout.setSelectedIndicatorColors(res.getColor(R.color.tab_selected_strip));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        if (mSlidingTabLayout != null) {
            mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        UpdateChecker updateChecker = new UpdateChecker(this);
        updateChecker.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_refresh:
                requestRefresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_HOME;
    }

    @Override
    public void onFragmentViewCreated(BaseMovieFragment fragment) {

    }

    @Override
    public void onFragmentAttached(BaseMovieFragment fragment) {
        mMoviesFragments.add(fragment);
    }

    @Override
    public void onFragmentDetached(BaseMovieFragment fragment) {
        mMoviesFragments.remove(fragment);
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {

        for (BaseMovieFragment fragment : mMoviesFragments) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (!fragment.getUserVisibleHint()) {
                    continue;
                }
            }

            return ViewCompat.canScrollVertically(fragment.getGridView(), -1);
        }

        return false;
    }

    protected void requestRefresh(){
        for(BaseMovieFragment fragment : mMoviesFragments){
            fragment.refreshMovieData(); // todo: call this with event bus
        }
    }

    private class MainTabsViewPagerAdapter extends FragmentPagerAdapter {

        public MainTabsViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 1:
                    return new PopularMoviesFragment();
                case 2:
                    return new UpcomingMoviesFragment();
                default:
                    return new LatestMoviesFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 1:
                    return getString(R.string.tab_popular);
                case 2:
                    return getString(R.string.tab_upcoming);
                default:
                    return getString(R.string.tab_latest);
            }
        }
    }
}
