package com.gbaldera.yts.activities;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gbaldera.yts.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class BaseDrawerActivity extends BaseActivity
        implements AdapterView.OnItemClickListener {

    protected static final int NAVDRAWER_ITEM_HOME = 0;
    protected static final int NAVDRAWER_ITEM_SEARCH = 1;
    protected static final int NAVDRAWER_ITEM_REQUESTS = 2;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 4;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_home,
            R.string.navdrawer_item_search,
            R.string.navdrawer_item_requests,
            0,
            R.string.navdrawer_item_settings
    };

    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_drawer_home,
            R.drawable.ic_drawer_search,
            R.drawable.ic_drawer_requests,
            0,
            R.drawable.ic_drawer_settings
    };

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private DrawerAdapter mDrawerAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();
    }

    @Override
    protected void onStart(){
        super.onStart();

        //setDrawerSelectedItem(getSelfNavDrawerItem());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupDrawer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(mDrawerToggle != null){
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onItemClick(android.widget.AdapterView<?> parent, android.view.View view,
                            int position, long id){

        if (position == getSelfNavDrawerItem()) {
            mDrawerLayout.closeDrawer(Gravity.START);
            return;
        }

        final int itemId = position;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(itemId);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        mDrawerLayout.closeDrawer(Gravity.START);
    }

    private void goToNavDrawerItem(int item) {
        Intent intent = null;

        switch (item){
            case NAVDRAWER_ITEM_HOME:
                intent = new Intent(this, MainActivity.class);
                break;
            case NAVDRAWER_ITEM_SEARCH:
                intent = new Intent(this, SearchActivity.class);
                break;
            case NAVDRAWER_ITEM_SETTINGS:
                intent = new Intent(this, SettingsActivity.class);
                break;
        }

        if(intent != null){
            startActivity(intent);
            overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_fade_out);
        }
    }


    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses
     * of BaseActivity override this to indicate what nav drawer item corresponds to them
     * Return NAVDRAWER_ITEM_INVALID to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Highlights the given position in the drawer menu. Activities listed in the drawer should call
     * this in {@link #onStart()}.
     */
    public void setDrawerSelectedItem(int menuItemPosition) {
        mDrawerList.setItemChecked(menuItemPosition, true);
    }

    private void setupDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        if(mActionBarToolbar != null){
            mActionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            });

            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mActionBarToolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        else{
            mDrawerToggle = null;
            mDrawerLayout.setDrawerListener(null);
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        populateNavDrawer();
    }

    private void populateNavDrawer() {

        mDrawerList = (ListView) findViewById(R.id.navdrawer_items_list);
        mDrawerAdapter = new DrawerAdapter(this);

        mDrawerAdapter.add(new DrawerItem(NAVDRAWER_TITLE_RES_ID[NAVDRAWER_ITEM_HOME],
                NAVDRAWER_ICON_RES_ID[NAVDRAWER_ITEM_HOME]));
        mDrawerAdapter.add(new DrawerItem(NAVDRAWER_TITLE_RES_ID[NAVDRAWER_ITEM_SEARCH],
                NAVDRAWER_ICON_RES_ID[NAVDRAWER_ITEM_SEARCH]));
        mDrawerAdapter.add(new DrawerItem(NAVDRAWER_TITLE_RES_ID[NAVDRAWER_ITEM_REQUESTS],
                NAVDRAWER_ICON_RES_ID[NAVDRAWER_ITEM_REQUESTS]));

        mDrawerAdapter.add(new DrawerItemSeparator());

        mDrawerAdapter.add(new DrawerItem(NAVDRAWER_TITLE_RES_ID[NAVDRAWER_ITEM_SETTINGS],
                NAVDRAWER_ICON_RES_ID[NAVDRAWER_ITEM_SETTINGS]));

        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(this);

        setDrawerSelectedItem(getSelfNavDrawerItem());
    }

    public boolean isNavDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.START);
    }

    public void openNavDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeNavDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    public boolean toggleDrawer(MenuItem item) {
        if (item != null && item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return false;
    }

    public class DrawerItem {

        String mTitle;
        int mIconRes;

        public DrawerItem(int titleRes, int iconRes) {
            mTitle = titleRes == 0 ? null : getString(titleRes);
            mIconRes = iconRes;
        }
    }

    private class DrawerItemSeparator extends DrawerItem {

        public DrawerItemSeparator() {
            super(0, 0);
        }
    }

    public static class DrawerAdapter extends ArrayAdapter<Object> {

        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_SEPARATOR = 1;

        public DrawerAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof DrawerItemSeparator ? 1 : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Object item = getItem(position);

            if (getItemViewType(position) == VIEW_TYPE_SEPARATOR) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.navdrawer_separator, parent, false);
                return convertView;
            }

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.navdrawer_item, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            DrawerItem menuItem = (DrawerItem) item;
            holder.icon.setImageResource(menuItem.mIconRes);
            holder.title.setText(menuItem.mTitle);

            return convertView;
        }
    }

    public static class ViewHolder {

        @InjectView(R.id.title)
        TextView title;

        @InjectView(R.id.icon)
        ImageView icon;

        public ViewHolder(View v){
            ButterKnife.inject(this, v);
        }
    }
}
