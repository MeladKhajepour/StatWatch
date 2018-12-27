package com.example.android.statwatch.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.android.statwatch.R;

public class Resources {
    static Drawable accentToGreyTransition;
    static Drawable greyToAccentTransition;
    static Drawable backgroundColour;

    public Resources(Context ctx) {
        android.content.res.Resources res = ctx.getResources();

        accentToGreyTransition = res.getDrawable(R.drawable.accent_to_grey_transition);
        greyToAccentTransition = res.getDrawable(R.drawable.grey_to_accent_transition);
        backgroundColour = res.getDrawable(R.color.background_colour);
    }
}
