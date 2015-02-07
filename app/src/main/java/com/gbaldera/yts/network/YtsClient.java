package com.gbaldera.yts.network;


import android.content.Context;

import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovieDetails;
import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.models.YtsMovieListUpcoming;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Query;

public class YtsClient {

    private static final String API_URL = "https://yts.re/api/v2";

    public interface YtsService {

        @GET("/list_upcoming.json")
        YtsMovieListUpcoming list_upcoming();

        @GET("/list_movies.json")
        YtsMovieList list_movies(@Query("limit") int limit, @Query("page") int page,
                          @Query("quality") YtsMovieQuality quality, @Query("minimum_rating") int rating,
                          @Query("query_term") String query, @Query("genre") YtsMovieGenre genre,
                          @Query("sort_by") YtsMovieSort sort, @Query("order_by") YtsMovieOrder order,
                          @Query("with_rt_ratings") boolean with_rt_ratings);

        @GET("/listimdb.json")
        YtsMovieList listimdb(@Query("imdb_id") String imdb_id);

        @GET("/movie_details.json")
        YtsMovieDetails movie_details(@Query("movie_id") int movie_id,
                                      @Query("with_images") boolean with_images,
                                      @Query("with_images") boolean with_cast);
    }

    public static YtsService create(Context context) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient(ServicesHelper.getCachingOkHttpClient(context)))
                .build();

        return restAdapter.create(YtsService.class);
    }
}
