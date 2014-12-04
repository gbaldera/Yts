package com.gbaldera.yts.enumerations;


public enum YtsMovieQuality {

    ALL("ALL");

    private final String value;

    private YtsMovieQuality(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
