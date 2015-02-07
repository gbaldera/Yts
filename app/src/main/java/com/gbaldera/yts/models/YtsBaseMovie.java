package com.gbaldera.yts.models;


public abstract class YtsBaseMovie {
    public int id;
    public String url;
    public String imdb_code;
    public String title;
    public String title_long;
    public int year;
    public String small_cover_image;
    public String medium_cover_image;
    public String date_uploaded;
    public int date_uploaded_unix;
}
