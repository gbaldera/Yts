package com.gbaldera.yts.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.widget.Toast;

import com.gbaldera.yts.BuildConfig;
import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.SettingsHelper;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.UpdateCheckerResult;

import it.gmariotti.changelibs.library.view.ChangeLogListView;
import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener, UpdateCheckerResult {

    public static final String PREF_KEY_ENABLE_NOTIFICATIONS = "pref_key_enable_notifications";
    public static final String PREF_KEY_OPEN_SOURCE_LICENSES = "pref_key_open_source_licenses";
    public static final String PREF_KEY_APP_WEBSITE = "pref_key_app_website";
    public static final String PREF_KEY_CHECK_FOR_UPDATE = "pref_key_check_for_update";
    public static final String PREF_KEY_CHANGELOG = "pref_key_changelog";
    public static final String PREF_KEY_APP_VERSION = "pref_key_version";

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final Activity activity = getActivity();

        // set open source dialog
        Preference opensourcePreference = findPreference(PREF_KEY_OPEN_SOURCE_LICENSES);
        opensourcePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open licenses dialog
                showOpenSourceLicenses(activity);
                return true;
            }
        });

        // set app website
        Preference appWebsitePreference = findPreference(PREF_KEY_APP_WEBSITE);
        appWebsitePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://gbaldera.github.com/Yts"));
                activity.startActivity(intent);
                return true;
            }
        });

        // check for update
        final UpdateCheckerResult updateCheckerResult = this;
        Preference checkForUpdatePreference = findPreference(PREF_KEY_CHECK_FOR_UPDATE);
        checkForUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                UpdateChecker updateChecker = new UpdateChecker(getActivity(), updateCheckerResult);
                updateChecker.setIgnoreSuccessfulChecks(true);
                updateChecker.start();
                return true;
            }
        });

        // changelog
        Preference changelogPreference = findPreference(PREF_KEY_CHANGELOG);
        changelogPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showChangelog(activity);
                return true;
            }
        });

        // app version
        Preference appVersion = findPreference(PREF_KEY_APP_VERSION);
        appVersion.setSummary("v" + BuildConfig.VERSION_NAME);

        SettingsHelper.registerOnSharedPreferenceChangeListener(getActivity(), this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SettingsHelper.unregisterOnSharedPreferenceChangeListener(getActivity(), this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PREF_KEY_ENABLE_NOTIFICATIONS)){
            boolean notificationsEnabled = sharedPreferences.getBoolean(PREF_KEY_ENABLE_NOTIFICATIONS, true);
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
            else {
                ParsePush.unsubscribeInBackground("", new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Timber.d("successfully un-subscribed from the broadcast channel.");
                        } else {
                            Timber.e(e, "failed to un-subscribe for push");
                        }
                    }
                });
            }
        }
    }

    public static void showOpenSourceLicenses(Activity activity) {
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog_licenses");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new OpenSourceLicensesDialog().show(ft, "dialog_licenses");
    }

    public static void showChangelog(Activity activity){
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        new ChangelogDialogFragment().show(ft, "changelogdemo_dialog");
    }

    public static void showUpdaterToast(Activity activity, int stringResource){
        Toast.makeText(activity, activity.getString(stringResource),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void foundUpdateAndShowIt(String versionDonwloadable) {

    }

    @Override
    public void foundUpdateAndDontShowIt(String versionDonwloadable) {

    }

    @Override
    public void returnUpToDate(String versionDonwloadable) {
        showUpdaterToast(getActivity(), com.rampo.updatechecker.R.string.no_update_found);
    }

    @Override
    public void returnMultipleApksPublished() {
        showUpdaterToast(getActivity(), com.rampo.updatechecker.R.string.network_error);
    }

    @Override
    public void returnNetworkError() {
        showUpdaterToast(getActivity(), com.rampo.updatechecker.R.string.network_error);
    }

    @Override
    public void returnAppUnpublished() {
        showUpdaterToast(getActivity(), com.rampo.updatechecker.R.string.network_error);
    }

    @Override
    public void returnStoreError() {
        showUpdaterToast(getActivity(), com.rampo.updatechecker.R.string.network_error);
    }

    public static class OpenSourceLicensesDialog extends DialogFragment {

        public OpenSourceLicensesDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/licenses.html");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pref_title_open_source_licenses)
                    .setView(webView)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

    public static class ChangelogDialogFragment extends DialogFragment {

        public ChangelogDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            ChangeLogListView chgList=(ChangeLogListView)layoutInflater.inflate(R.layout.fragment_changelog_dialog, null);

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pref_title_changelog)
                    .setView(chgList)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();

        }

    }
}
