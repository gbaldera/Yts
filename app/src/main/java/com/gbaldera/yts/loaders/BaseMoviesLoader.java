package com.gbaldera.yts.loaders;


import android.content.Context;

import java.util.List;

import com.gbaldera.yts.models.YtsMovie;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

public abstract class BaseMoviesLoader<T> extends GenericSimpleLoader<T> {

    public BaseMoviesLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        return null;
    }

    protected abstract List<YtsMovie> getMoviesFromYts();
}
