package com.gbaldera.yts.enumerations;


public enum YtsMovieSort {

    DATE("date"),
    SEEDS("seeds"),
    PEERS("peers"),
    SIZE("size"),
    ALPHABET("alphabet"),
    RATING("rating"),
    DOWNLOADED("downloaded"),
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
