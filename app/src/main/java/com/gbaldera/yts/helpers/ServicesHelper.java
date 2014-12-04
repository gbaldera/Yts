package com.gbaldera.yts.helpers;


import android.content.Context;
import android.os.StatFs;

import com.gbaldera.yts.BuildConfig;
import com.gbaldera.yts.network.TraktClient;
import com.gbaldera.yts.network.YtsClient;
import com.jakewharton.trakt.Trakt;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public final class ServicesHelper {

    static final int CONNECT_TIMEOUT_MILLIS = 25 * 1000; // 25s
    static final int READ_TIMEOUT_MILLIS = 30 * 1000; // 30s
    private static final String API_CACHE = "yts-cache";
    private static final int MIN_DISK_API_CACHE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int MAX_DISK_API_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private static OkHttpClient httpClient;
    private static OkUrlFactory urlFactory;
    private static OkHttpClient cachingHttpClient;
    private static OkUrlFactory cachingUrlFactory;

    private static Trakt trakt;
    private static YtsClient.YtsService ytsService;

    /**
     * Returns this apps {@link com.squareup.okhttp.OkHttpClient} with no cache enabled.
     */
    public static synchronized OkHttpClient getOkHttpClient() {
        if (httpClient == null) {
            httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            httpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }
        return httpClient;
    }

    /**
     * Returns this apps {@link com.squareup.okhttp.OkHttpClient} with enabled response cache.
     * Should be used with API calls.
     */
    public static synchronized OkHttpClient getCachingOkHttpClient(Context context) {
        if (cachingHttpClient == null) {
            cachingHttpClient = new OkHttpClient();
            cachingHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            cachingHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            File cacheDir = createApiCacheDir(context);
            try {
                cachingHttpClient.setCache(
                        new Cache(cacheDir, calculateApiDiskCacheSize(cacheDir)));
            } catch (IOException ignored) {
            }
        }
        return cachingHttpClient;
    }

    static File createApiCacheDir(Context context) {
        File cache = new File(context.getApplicationContext().getCacheDir(), API_CACHE);
        if (!cache.exists()) {
            cache.mkdirs();
        }
        return cache;
    }

    static long calculateApiDiskCacheSize(File dir) {
        long size = MIN_DISK_API_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available = ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_API_CACHE_SIZE), MIN_DISK_API_CACHE_SIZE);
    }

    public static synchronized Trakt getTrakt(Context context) {
        if (trakt == null) {
            trakt = new TraktClient(context).setApiKey(BuildConfig.TRAKT_API_KEY);
        }
        return trakt;
    }

    public static synchronized YtsClient.YtsService getYtsService(Context context) {
        if (ytsService == null) {
            ytsService = YtsClient.create(context);
        }
        return ytsService;
    }
}
