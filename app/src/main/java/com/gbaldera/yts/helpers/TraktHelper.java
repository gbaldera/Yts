package com.gbaldera.yts.helpers;


import java.net.URL;

public class TraktHelper {

    public enum TraktImageSize {
        FULL("original"),
        MEDIUM("medium"),
        THUMB("thumb");

        private final String value;

        private TraktImageSize(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum TraktImageType {
        POSTER("poster"),
        FANART("fanart"),
        HEADSHOT("headshot"),
        SCREEN("screen");

        private final String value;

        private TraktImageType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static final int POSTER_SIZE_SPEC_138 = 138;
    public static final int POSTER_SIZE_SPEC_300 = 300;
    public static final int FAN_ART_SIZE_SPEC_218 = 218;
    public static final int FAN_ART_SIZE_SPEC_940 = 940;

    public static final String DEFAULT_BASE_POSTERS_URL = "http://trakt.us/images/posters/%d-%d.jpg";

    public static String resizeImage(String imageUrl, int width){

        String resizedUrl = imageUrl;

        try{
            URL url = new URL(imageUrl);

            //String fileName = imageUrl.substring(imageUrl.lastIndexOf('/')+1, imageUrl.length());

            String path = url.getPath();
            String pathWithoutExt = path.substring(0, path.lastIndexOf('.'));
            String extension = path.substring(path.lastIndexOf('.')+1);

            resizedUrl = url.getProtocol() + "://" + url.getHost()  + pathWithoutExt + "-" + width + "." + extension;
        }
        catch (Exception ex){}

        return resizedUrl;
    }

    /**
     *
     * Temporary and dumb method to resize v2 trakt images
     * */
    public static String resizeImage(com.jakewharton.trakt.entities.Movie movie, TraktImageType type,
                                     TraktImageSize size){
        if(movie != null && movie.images != null){
            String imageUrl;
            switch (type){
                case FANART:
                    imageUrl = movie.images.fanart;
                    break;
                case HEADSHOT:
                    imageUrl = movie.images.headshot;
                    break;
                case SCREEN:
                    imageUrl = movie.images.screen;
                    break;
                default:
                    imageUrl = movie.images.poster;
                    break;
            }

            return imageUrl.replace("original", size.toString());
        }

        return null;
    }
}
