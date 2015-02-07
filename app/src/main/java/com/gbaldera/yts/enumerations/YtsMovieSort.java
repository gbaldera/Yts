package com.gbaldera.yts.enumerations;


public enum YtsMovieSort {

    TITLE("title"),
    DATE("date_added"),
    SEEDS("seeds"),
    PEERS("peers"),
    RATING("rating"),
    DOWNLOADED("downloaded_count"),
    LIKE("like_count"),
    YEAR("year");

    private final String value;

    private YtsMovieSort(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
