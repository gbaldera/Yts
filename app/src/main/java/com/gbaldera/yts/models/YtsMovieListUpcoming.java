package com.gbaldera.yts.models;


import java.util.List;

public class YtsMovieListUpcoming extends YtsBaseModel {
    public YtsMovieListUpcomingData data;

    public class YtsMovieListUpcomingData {
        public int upcoming_movies_count;
        public List<YtsMovie> upcoming_movies;
    }
}
