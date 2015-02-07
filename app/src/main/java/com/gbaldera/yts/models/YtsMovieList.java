package com.gbaldera.yts.models;


import java.util.List;

public class YtsMovieList extends YtsBaseModel {
    public YtsMovieListData data;

    public class YtsMovieListData {
        public int movie_count;
        public int limit;
        public int page_number;
        public List<YtsMovie> movies;
    }
}
