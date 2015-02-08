package com.gbaldera.yts.models;


import java.util.List;

public class YtsMovieComments extends YtsBaseModel {
    public YtsMovieCommentsData data;

    public class YtsMovieCommentsData {
        public int comment_count;
        public List<YtsMovieComment> comments;
    }
}
