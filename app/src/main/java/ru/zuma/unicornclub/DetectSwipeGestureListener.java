package ru.zuma.unicornclub;

import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Jerry on 4/18/2018.
 */

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    // Minimal x and y axis swipe distance in CM.
    private float minHorizontalSwipeDist = 0.8f;

    // Maximal x and y axis swipe distance in CM.
    private float minVerticalSwipeDist = 1.2f;

    // Source activity that display message in text view.
    private UnicornImageActivity activity = null;

    public DetectSwipeGestureListener(UnicornImageActivity activity) {
        this.activity = activity;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX) / displayMetrics.xdpi * 2.54f;
        float deltaYAbs = Math.abs(deltaY) / displayMetrics.ydpi * 2.54f;

        if (deltaXAbs >= minHorizontalSwipeDist) {
            if (deltaX > 0) {
                activity.onHorizontalSwipe(false);
            } else {
                activity.onHorizontalSwipe(true);
            }
        }

        if (deltaYAbs >= minVerticalSwipeDist) {
            activity.onVerticalSwipe();
        }

        return true;
    }
}
