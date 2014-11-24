package com.gbaldera.yts.models;


public class YtsMovie {
    public int MovieID;
    public String State;
    public String MovieUrl;
    public String MovieTitle;
    public String MovieTitleClean;
    public int MovieYear;
    public String DateUploaded;
    public int DateUploadedEpoch;
    public String Quality;
    public String CoverImage;
    public String ImdbCode;
    public String ImdbLink;
    public String Size;
    public String SizeByte;
    public String MovieRating;
    public String Genre;
    public String Uploader;
    public String UploaderUID;
    public String Downloaded;
    public String TorrentSeeds;
    public String TorrentPeers;
    public String TorrentUrl;
    public String TorrentHash;
    public String TorrentMagnetUrl;

    public YtsMovie(){}

    public YtsMovie(YtsUpcomingMovie upcomingMovie){
        MovieTitle = upcomingMovie.MovieTitle;
        DateUploaded = upcomingMovie.DateAdded;
        DateUploadedEpoch = upcomingMovie.DateAddedEpoch;
        CoverImage = upcomingMovie.MovieCover;
        ImdbCode = upcomingMovie.ImdbCode;
        ImdbLink = upcomingMovie.ImdbLink;
        Uploader = upcomingMovie.Uploader;
        UploaderUID = upcomingMovie.UploaderUID;
    }
}
