package com.example.admin.ebeliefapp;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by root on 13/6/16.
 */
public class MiscUtils {
    protected static int getColor(Context context, int color) {
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(color, tv, true);
        return tv.data;
    }

    public static int dpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();

        try {
            return (int) (dp * (metrics.densityDpi / 160f));
        } catch (NoSuchFieldError ignored) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
        }
    }

    /**
     * Returns screen width.
     *
     * @param context Context to get resources and device specific display metrics
     * @return screen width
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (displayMetrics.widthPixels / displayMetrics.density);
    }


    /**
     * A method for animating width for the tabs.
     *
     * @param tab   tab to animate.
     * @param start starting width.
     * @param end   final width after animation.
     */
    public static void resizeTab(final View tab, float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(150);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ViewGroup.LayoutParams params = tab.getLayoutParams();
                if (params == null) return;

                params.width = Math.round((float) animator.getAnimatedValue());
                tab.setLayoutParams(params);
            }
        });
        animator.start();
    }

    public static String getString(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    public static void displayConfirmationDialog(Context context, String title,
                                                 String message, String positiveButtonLabel,
                                                 DialogInterface.OnClickListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder = builder.setTitle(title).setMessage(message)
                .setPositiveButton(positiveButtonLabel, listener);
        builder.setCancelable(false);
        builder.show();
    }
}