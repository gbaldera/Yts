package com.gbaldera.yts.enumerations;


public enum YtsMovieGenre {

    ALL("ALL");

    private final String value;

    private YtsMovieGenre(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
