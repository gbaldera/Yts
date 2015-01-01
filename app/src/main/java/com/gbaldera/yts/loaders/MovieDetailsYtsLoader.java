package com.gbaldera.yts.loaders;


import android.content.Context;

import com.gbaldera.yts.helpers.ServicesHelper;
import com.gbaldera.yts.models.YtsMovie;
import com.gbaldera.yts.models.YtsMovieDetails;
import com.gbaldera.yts.models.YtsMovieDetailsSummary;
import com.gbaldera.yts.models.YtsMovieList;
import com.gbaldera.yts.network.YtsClient;
import com.uwetrottmann.androidutils.GenericSimpleLoader;

import java.util.ArrayList;

import timber.log.Timber;

public class MovieDetailsYtsLoader extends GenericSimpleLoader<YtsMovieDetailsSummary> {
    private String imdbId;

    public MovieDetailsYtsLoader(Context context, String imdbId) {
        super(context);
        this.imdbId = imdbId;
    }

    @Override
    public YtsMovieDetailsSummary loadInBackground() {
        try{
            YtsClient.YtsService ytsService = ServicesHelper.getYtsService(getContext());
            YtsMovieList movieList = ytsService.listimdb(imdbId);
            YtsMovieDetailsSummary movieDetailsSummary = new YtsMovieDetailsSummary();

            YtsMovie firstMovie = movieList.MovieList.get(0);
            YtsMovieDetails movieDetails = ytsService.movie(firstMovie.MovieID);

            movieDetailsSummary.MovieList = movieList.MovieList;
            movieDetailsSummary.YoutubeTrailerID = movieDetails.YoutubeTrailerID;
            movieDetailsSummary.YoutubeTrailerUrl = movieDetails.YoutubeTrailerUrl;
            movieDetailsSummary.Genre1 = movieDetails.Genre1;
            movieDetailsSummary.Genre2 = movieDetails.Genre2;

            movieDetailsSummary.MovieScreenshots = new ArrayList<>();
            movieDetailsSummary.MovieScreenshots.add(movieDetails.LargeScreenshot1);
            movieDetailsSummary.MovieScreenshots.add(movieDetails.LargeScreenshot2);
            movieDetailsSummary.MovieScreenshots.add(movieDetails.LargeScreenshot3);

            return movieDetailsSummary;
        }
        catch (Exception e){
            Timber.e(e, "Downloading movie details from Yts failed");
        }
        return null;
    }
}
