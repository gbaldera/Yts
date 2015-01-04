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
import com.github.underscore._;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.enumerations.Extended2;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchMoviesLoader extends GenericSimpleLoader<List<Movie>> {

    private String mQuery = "";

    public SearchMoviesLoader(Context context, String query) {
        super(context);
        mQuery = query;
    }

    @Override
    public List<Movie> loadInBackground() {
        if(!TextUtils.isEmpty(mQuery)){
            try{
                YtsMovieList ytsMovieList = ServicesHelper.getYtsService(getContext()).
                        list(50, 1, YtsMovieQuality.ALL, 0, mQuery, YtsMovieGenre.ALL,
                                YtsMovieSort.DATE, YtsMovieOrder.DESC);

                Timber.d("Searching Yts for: " + mQuery);

                if(ytsMovieList.MovieCount == 0){
                    return null;
                }

                Timber.d("Total results: " + ytsMovieList.MovieCount);

                List<Object> imbIdObjects = _.uniq(_.pluck(ytsMovieList.MovieList, "ImdbCode"));
                List<String> imbIds = new ArrayList<String>();

                for(Object object : imbIdObjects){
                    if (object == null)
                        continue;
                    imbIds.add(object.toString());
                }

                return ServicesHelper.getTrakt(getContext())
                        .movieService().summaries(_.join(imbIds, ","), Extended2.FULL);
            }
            catch (Exception e){
                Timber.e(e, "Searching movie list failed");
            }
        }
        return null;
    }
}
