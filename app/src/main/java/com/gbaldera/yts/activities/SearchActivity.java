package com.gbaldera.yts.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gbaldera.yts.R;
import com.gbaldera.yts.fragments.SearchMoviesFragment;

public class SearchActivity extends BaseDrawerActivity {

    private SearchMoviesFragment mSearchFragment = null;
    private SearchView mSearchView = null;
    private String mQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (savedInstanceState == null) {
            mSearchFragment = new SearchMoviesFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mSearchFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if(searchItem != null){
            mSearchView = (SearchView) searchItem.getActionView();
            mSearchView.setIconifiedByDefault(false);
            mSearchView.setQueryRefinementEnabled(true);
            mSearchView.setQueryHint(getString(R.string.search_hint));

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (mSearchView != null) {
                        mSearchView.clearFocus();
                    }

                    if(mSearchFragment != null){
                        mSearchFragment.updateSearch(query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (TextUtils.isEmpty(query) && mSearchFragment != null) {
                        mSearchFragment.clearSearch();
                    }
                    return false;
                }
            });

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    finish();
                    return false;
                }
            });

            if (!TextUtils.isEmpty(mQuery)) {
                mSearchView.setQuery(mQuery, false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_SEARCH;
    }
}
