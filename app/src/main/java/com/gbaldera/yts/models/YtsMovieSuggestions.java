package com.gbaldera.yts.models;


import java.util.List;

public class YtsMovieSuggestions extends YtsBaseModel {
    public YtsMovieSuggestionsData data;

    public class YtsMovieSuggestionsData {
        public int movie_suggestions_count;
        public List<YtsMovie> movie_suggestions;
    }
}
