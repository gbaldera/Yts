package com.gbaldera.yts.network;


import android.content.Context;

import com.gbaldera.yts.enumerations.YtsMovieGenre;
import com.gbaldera.yts.enumerations.YtsMovieOrder;
import com.gbaldera.yts.enumerations.YtsMovieQuality;
import com.gbaldera.yts.enumerations.YtsMovieSort;
import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsLoginResult;
import com.gbaldera.yts.models.YtsMovieComments;
import com.gbaldera.yts.models.YtsMovieDetails;
import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.models.YtsMovieListUpcoming;
import com.gbaldera.yts.models.YtsMovieSuggestions;
import com.gbaldera.yts.models.YtsProfile;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class YtsClient {

    private static final String API_URL = "https://yts.to/api/v2";

    public interface YtsService {

        @GET("/list_upcoming.json")
        YtsMovieListUpcoming list_upcoming();

        @GET("/list_movies.json")
        YtsMovieList list_movies(@Query("limit") int limit, @Query("page") int page,
                          @Query("quality") YtsMovieQuality quality, @Query("minimum_rating") int rating,
                          @Query("query_term") String query, @Query("genre") YtsMovieGenre genre,
                          @Query("sort_by") YtsMovieSort sort, @Query("order_by") YtsMovieOrder order,
                          @Query("with_rt_ratings") boolean with_rt_ratings);

        @GET("/movie_details.json")
        YtsMovieDetails movie_details(@Query("movie_id") int movie_id,
                                      @Query("with_images") boolean with_images,
                                      @Query("with_cast") boolean with_cast);

        @GET("/movie_suggestions.json")
        YtsMovieSuggestions movie_suggestions(@Query("movie_id") int movie_id);

        @GET("/movie_comments.json")
        YtsMovieComments movie_comments(@Query("movie_id") int movie_id);

        @POST("/make_comment.json")
        YtsProfile make_comment(@Field("user_key") String user_key, @Field("movie_id") int movie_id,
                                @Field("comment_text") String comment_text,
                                @Field("application_key") String application_key);

        @POST("/user_get_key.json")
        YtsLoginResult login(@Field("username") String username, @Field("password") String password,
                             @Field("application_key") String application_key);

        @POST("/user_register.json")
        YtsLoginResult register(@Field("username") String username, @Field("password") String password,
                             @Field("email") String email, @Field("application_key") String application_key);

        @GET("/user_profile.json")
        YtsProfile profile(@Query("user_key") String user_key);
    }

    public static YtsService create(Context context) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setClient(new OkClient(ServicesHelper.getCachingOkHttpClient(context)))
                .build();

        return restAdapter.create(YtsService.class);
    }
}
