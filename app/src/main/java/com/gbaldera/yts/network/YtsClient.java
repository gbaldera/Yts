package com.gbaldera.yts.network;


import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.models.YtsUpcomingMovie;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class YtsClient {

    private static final String API_URL = "http://yts.re/api";

    public interface YtsService {

        @GET("/upcoming.json")
        void upcomingMovies(Callback<List<YtsUpcomingMovie>> callback);

        @GET("/list.json")
        void listMovies(@Query("limit") int limit, @Query("set") int set,
                        @Query("quality") String quality, @Query("rating") int rating,
                        @Query("keywords") String keywords, @Query("genre") String genre,
                        @Query("sort") String sort, @Query("order") String order,
                        Callback<YtsMovieList> callback);
    }

    public static YtsService create() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .build();

        return restAdapter.create(YtsService.class);
    }
}
