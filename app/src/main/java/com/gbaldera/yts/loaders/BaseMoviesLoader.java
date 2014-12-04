package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.models.YtsMovie;
import com.github.underscore._;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public abstract class BaseMoviesLoader<T> extends GenericSimpleLoader<T> {

    public BaseMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        try{
            List<YtsMovie> movieList = getMoviesFromYts();

            List<Object> imbIdObjects = _.uniq(_.pluck(movieList, "ImdbCode"));
            List<String> imbIds = new ArrayList<String>();

            for(Object object : imbIdObjects){
                if (object == null)
                    continue;
                imbIds.add(object.toString());
            }

            return getMoviesFromTrakt(imbIds);
        }
        catch (Exception e){
            Timber.e(e, "Downloading movies failed");
        }

        return null;
    }

    protected abstract T getMoviesFromTrakt(List<String> imbIds);

    protected abstract List<YtsMovie> getMoviesFromYts();
}
