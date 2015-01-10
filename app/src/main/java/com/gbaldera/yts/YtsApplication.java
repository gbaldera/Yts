package com.gbaldera.yts;


import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.gbaldera.yts.fragments.SettingsFragment;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import io.fabric.sdk.android.Fabric;
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
            if (!Fabric.isInitialized()) {
                Fabric.with(this, new Crashlytics());
            }
        }
    }
}
