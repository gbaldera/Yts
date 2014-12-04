package com.gbaldera.yts.network;


import android.content.Context;

import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.models.YtsUpcomingMovie;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;

public class YtsClient {

    private static final String API_URL = "https://yts.re/api";

    public interface YtsService {

        @GET("/upcoming.json")
        List<YtsUpcomingMovie> upcoming();

        @GET("/list.json")
        YtsMovieList list(@Query("limit") int limit, @Query("set") int set,
                          @Query("quality") YtsMovieQuality quality, @Query("rating") int rating,
                          @Query("keywords") String keywords, @Query("genre") YtsMovieGenre genre,
                          @Query("sort") YtsMovieSort sort, @Query("order") YtsMovieOrder order);

        @GET("/listimdb.json")
        YtsMovieList listimdb(@Query("imdb_id") String imdb_id);

        @GET("/listimdb.json")
        YtsMovieList listimdb(@Query("imdb_id") List<String> imdb_id);


    }

    public static YtsService create(Context context) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient(ServicesHelper.getCachingOkHttpClient(context)))
                .build();

        return restAdapter.create(YtsService.class);
    }
}
