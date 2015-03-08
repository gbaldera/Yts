package com.gbaldera.yts.helpers;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.TextView;

import com.gbaldera.yts.R;

public class ColorHelper {

    public static int darken(final int color, float fraction) {
        return blendColors(Color.BLACK, color, fraction);
    }

    public static int lighten(final int color, float fraction) {
        return blendColors(Color.WHITE, color, fraction);
    }

    /**
     * @return luma value according to to YIQ color space.
     */
    public static final int calculateYiqLuma(int color) {
        return Math.round((
                299 * Color.red(color) +
                        587 * Color.green(color) +
                        114 * Color.blue(color))
                / 1000f);
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    public static int blendColors(int color1, int color2, float ratio) {
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRatio);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRatio);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRatio);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    public static final int changeBrightness(final int color, float fraction) {
        return calculateYiqLuma(color) >= 128
                ? darken(color, fraction)
                : lighten(color, fraction);
    }

    public static int modifyAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    /**
     * Taken from https://github.com/saulmm/Material-Movies/blob/master/HackVG%2Fapp%2Fsrc%2Fmain%2Fjava%2Fcom%2Fhackvg%2Fandroid%2Futils%2FGUIUtils.java#L30
     * **/
    public static void tintAndSetCompoundDrawable(Context context, @DrawableRes
    int drawableRes, int color, TextView textview) {

        Resources res = context.getResources();
        int padding = (int) res.getDimension(R.dimen.activity_horizontal_margin);

        Drawable drawable = res.getDrawable(drawableRes);
        drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);

        textview.setCompoundDrawablesWithIntrinsicBounds(drawable,
                null, null, null);

        textview.setCompoundDrawablePadding(padding);
    }
}

