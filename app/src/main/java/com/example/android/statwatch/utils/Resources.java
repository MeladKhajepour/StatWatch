package com.example.android.statwatch.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.android.statwatch.R;

public class Resources {
    static Drawable TRANSITION_ACCENT_TO_GREY;
    static Drawable TRANSITION_GREY_TO_ACCENT;
    static Drawable BUTTON_MAIN_GREY;

    public Resources(Context ctx) {
        android.content.res.Resources res = ctx.getResources();

        TRANSITION_ACCENT_TO_GREY = res.getDrawable(R.drawable.transition_accent_to_grey);
        TRANSITION_GREY_TO_ACCENT = res.getDrawable(R.drawable.transition_grey_to_accent);
        BUTTON_MAIN_GREY = res.getDrawable(R.drawable.button_main_grey);
    }
}
