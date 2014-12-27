package com.gbaldera.yts.helpers;


import android.content.Context;

import com.gbaldera.yts.R;

public class TextHelper {

    public static String getPrettyRuntime(Context context, int minutes) {
        if (minutes == 0) {
            return context.getString(R.string.stringNA);
        }

        int hours = (minutes / 60);
        minutes = (minutes % 60);

        if (hours > 0) {
            if (minutes == 0) {
                return hours + " " + context.getResources().getQuantityString(R.plurals.hour, hours, hours);
            } else {
                return hours + " " + context.getResources().getQuantityString(R.plurals.hour_short, hours, hours) + " " + minutes + " " + context.getResources().getQuantityString(R.plurals.minute_short, minutes, minutes);
            }
        } else {
            return minutes + " " + context.getResources().getQuantityString(R.plurals.minute, minutes, minutes);
        }
    }
}
