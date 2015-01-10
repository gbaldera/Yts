package com.gbaldera.yts;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gbaldera.yts.fragments.SettingsFragment;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import timber.log.Timber;

public class YtsApplication extends Application {
    @Override public void onCreate() {
        super.onCreate();

        // Initialize the Parse SDK.
        Parse.initialize(this, BuildConfig.PARSE_APP_ID, BuildConfig.PARSE_CLIENT_KEY);

        // Register for Push Notifications ?
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean notificationsEnabled =
                sharedPref.getBoolean(SettingsFragment.PREF_KEY_ENABLE_NOTIFICATIONS, true);
        if(notificationsEnabled){
            ParsePush.subscribeInBackground("", new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Timber.d("successfully subscribed to the broadcast channel.");
                    } else {
                        Timber.e(e, "failed to subscribe for push");
                    }
                }
            });
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }
}
