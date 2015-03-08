package com.gbaldera.yts.loaders;


import android.content.Context;
import android.text.TextUtils;

import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieList;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.List;

import timber.log.Timber;

public class SearchMoviesLoader extends GenericSimpleLoader<List<YtsMovie>> {

    private String mQuery = "";

    public SearchMoviesLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    public List<YtsMovie> loadInBackground() {
        if(!TextUtils.isEmpty(mQuery)){
            try{
                YtsMovieList ytsMovieList = ServicesHelper.getYtsService(getContext()).
                        list_movies(50, 1, YtsMovieQuality.ALL, 0, mQuery, YtsMovieGenre.ALL,
                                YtsMovieSort.DATE, YtsMovieOrder.DESC, false);

                Timber.d("Searching Yts for: " + mQuery);

                if(ytsMovieList.data.movie_count == 0){
                    return null;
                }

                Timber.d("Total results: " + ytsMovieList.data.movie_count);

                return ytsMovieList.data.movies;
            }
            catch (Exception e){
                Timber.e(e, "Searching movie list failed");
            }
        }
        return null;
    }
}
