package com.donnnno.android.helpers.core;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

/*
 * Android Helpers
 *
 * Copyright (c) 2017 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ColorHelper {

    @ColorInt
    public static int get(@NonNull Context context, int color) {
        try {
            return ContextCompat.getColor(context, color);
        } catch (Exception e) {
            return color;
        }
    }

    public static void setEdgeToEdge(@NonNull Activity activity, boolean isLight) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);

        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }

        setLightSystemBars(activity, isLight);
    }

    public static void setLightSystemBars(@NonNull Activity activity, boolean isLight) {
        Window window = activity.getWindow();
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(isLight);
            controller.setAppearanceLightNavigationBars(isLight);
        }
    }

    public static void applySystemBarsPadding(@NonNull View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            }
        });
    }

    public static void setStatusBarColor(@NonNull Context context, @ColorInt int color) {
        setStatusBarColor(context, color, false);
    }

    public static void setStatusBarColor(@NonNull Context context, @ColorInt int color, boolean transparent) {
        if (transparent) ((Activity) context).getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        ((Activity) context).getWindow().setStatusBarColor(color);
    }

    public static void setNavigationBarColor(@NonNull Context context, @ColorInt int color) {
        ((Activity) context).getWindow().setNavigationBarColor(color);
    }

    @ColorInt
    public static int getAttributeColor(Context context, @AttrRes int attr) {
        if (context == null) {
            Log.e("ColorHelper", "getAttributeColor() context is null");
            return Color.WHITE;
        }

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }

    @ColorInt
    public static int getTitleTextColor(@ColorInt int color) {
        double darkness = 1-(0.299* Color.red(color) + 0.587*Color.green(color) + 0.114*Color.blue(color))/255;
        return (darkness < 0.35) ? getDarkerColor(color, 0.25f) : Color.WHITE;
    }

    @ColorInt
    public static int getBodyTextColor(@ColorInt int color) {
        int title = getTitleTextColor(color);
        return setColorAlpha(title, 0.7f);
    }

    @ColorInt
    public static int getDarkerColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 1.0f) float transparency) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= transparency;
        return Color.HSVToColor(hsv);
    }

    @ColorInt
    public static int setColorAlpha(@ColorInt int color, @FloatRange(from = 0.0f, to = 1.0f) float alpha) {
        int alpha2 = Math.round(Color.alpha(color) * alpha);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha2, red, green, blue);
    }

    public static ColorStateList getColorStateList(@ColorInt int color) {
        int[][] states = new int[][] {
                new int[] {android.R.attr.state_pressed},
                new int[] {android.R.attr.state_focused},
                new int[] {}
        };
        int[] colors = new int[] {
                ColorHelper.getDarkerColor(color, 0.8f),
                ColorHelper.getDarkerColor(color, 0.8f),
                color
        };
        return new ColorStateList(states, colors);
    }

    public static ColorStateList getCheckedColorStateList(@ColorInt int unchecked, @ColorInt int checked) {
        int[][] states = new int[][] {
                new int[] {-android.R.attr.state_checked},
                new int[] {android.R.attr.state_checked},
        };

        int[] colors = new int[] {
                unchecked,
                checked
        };
        return new ColorStateList(states, colors);
    }

    public static boolean isLightColor(@ColorInt int color) {
        int title = ColorHelper.getTitleTextColor(color);
        return title != Color.WHITE;
    }

    public static void setupStatusBarIconColor(Context context) {
        if (context == null) {
            Log.e("ColorHelper", "setupStatusBarIconColor() context is null");
            return;
        }
        int color = getAttributeColor(context, R.attr.colorPrimaryDark);
        setupStatusBarIconColor(context, isLightColor(color));
    }

    public static void setupStatusBarIconColor(Context context, boolean isLightToolbar) {
        if (context == null) {
            Log.e("ColorHelper", "setupStatusBarIconColor() context is null");
            return;
        }

        Window window = ((Activity) context).getWindow();
        if (window == null) return;

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(isLightToolbar);
        }
    }

    public static boolean isValidColor(String string) {
        try {
            Color.parseColor(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
