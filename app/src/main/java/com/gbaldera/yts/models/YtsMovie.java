package com.gbaldera.yts.models;


import java.util.List;

public class YtsMovie extends YtsBaseMovie {
    public Double rating;
    public int runtime;
    public List<String> genres;
    public String language;
    public String mpa_rating;
    public String state;
    public int download_count;
    public int like_count;
    public int rt_critics_score;
    public int rt_audience_score;
    public String rt_critics_rating;
    public String rt_audience_rating;
    public String description_intro;
    public String description_full;
    public String yt_trailer_code;

    public List<YtsTorrent> torrents;
    public List<YtsMovieCast> directors;
    public List<YtsMovieCast> actors;
    public YtsMovieImages images;
}
