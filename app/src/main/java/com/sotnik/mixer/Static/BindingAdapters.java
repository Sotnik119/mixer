package com.sotnik.mixer.Static;

import android.databinding.BindingAdapter;
import android.widget.TextView;

public class BindingAdapters {
    /**
     * Необходим для вывода int в textview без .toString
     * @param view
     * @param value
     */
    @BindingAdapter("android:text")
    public static void intToText(TextView view, int value) {
        view.setText(String.valueOf(value));
    }
}
