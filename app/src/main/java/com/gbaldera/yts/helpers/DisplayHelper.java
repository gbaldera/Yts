package com.gbaldera.yts.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayHelper {

    /**
     * Returns true for all screens with dpi higher than {@link android.util.DisplayMetrics#DENSITY_HIGH}.
     */
    public static boolean isVeryHighDensityScreen(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi > DisplayMetrics.DENSITY_HIGH;
    }
}
