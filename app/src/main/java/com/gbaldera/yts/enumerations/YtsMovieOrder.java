package com.gbaldera.yts.enumerations;


public enum YtsMovieOrder {

    ASC("asc"),
    DESC("desc");

    private final String value;

    private YtsMovieOrder(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
