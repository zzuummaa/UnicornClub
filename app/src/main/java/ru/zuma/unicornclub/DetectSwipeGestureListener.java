package ru.zuma.unicornclub;

import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Jerry on 4/18/2018.
 */

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private DisplayMetrics displayMetrics;

    // Minimal x and y axis swipe distance in CM.
    private float minHorizontalSwipeDist;
    private float MIN_SWIPE_DISTANCE_Y;

    // Maximal x and y axis swipe distance in CM.
    private float MAX_SWIPE_DISTANCE_X;
    private float minVerticalSwipeDist;

    // Source activity that display message in text view.
    private UnicornImageActivity activity = null;

    public DetectSwipeGestureListener(UnicornImageActivity activity) {
        this.activity = activity;
        this.displayMetrics = activity.getResources().getDisplayMetrics();

        minHorizontalSwipeDist = displayMetrics.widthPixels * 0.2f;
        minVerticalSwipeDist = displayMetrics.heightPixels * 0.3f;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

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
