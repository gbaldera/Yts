package com.gbaldera.yts.network;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.uwetrottmann.trakt.v2.TraktV2;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class TraktClient extends TraktV2 {

    private final Context context;

    public TraktClient(Context context){
        this.context = context.getApplicationContext();
    }

    @Override
    protected RestAdapter.Builder newRestAdapterBuilder() {
        return new RestAdapter.Builder().setClient(
                new OkClient(ServicesHelper.getCachingOkHttpClient(context)));
    }

}
