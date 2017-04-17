package com.snaptiongame.snaption.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.snaptiongame.snaption.R;

/**
 * Created by brittanyberlanga on 3/8/17.
 */

public class MainFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private boolean snackbarVisible = false;
    private int screenHeightPx;
    private int minSnackbarHeightPx;
    private int fabMarginPx;

    public MainFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        screenHeightPx = res.getDisplayMetrics().heightPixels;
        minSnackbarHeightPx = screenHeightPx -
                res.getDimensionPixelSize(R.dimen.wall_bottom_navigation_height);
        fabMarginPx = res.getDimensionPixelSize(R.dimen.fab_margin);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof BottomNavigationView || dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Resources res = child.getResources();
        int[] location = new int[2];
        dependency.getLocationOnScreen(location);
        // if a snackbar is not visible, set the position of the fab relative to the bottom
        // navigation view
        if (!snackbarVisible && dependency instanceof BottomNavigationView) {
            // if the bottom navigation view is visible
            if (dependency.getVisibility() == View.VISIBLE) {
                setFabMarginWithDependency(child, location[1]);
            // else if the bottom navigation view is gone
            } else {
                setDefaultFabMargin(child);
            }
        }
        // if a snackbar is visible, set the position of the fab relative to the snackbar
        else if (dependency instanceof Snackbar.SnackbarLayout){
            // if the snackbar is above the bottom navigation view
            if (location[1] < minSnackbarHeightPx) {
                snackbarVisible = true;
                setFabMarginWithDependency(child, location[1]);
            }
            // else if the snackbar is at the same height or lower than the bottom navigation view
            else {
                snackbarVisible = false;
            }
        }
        return true;
    }

    private void setDefaultFabMargin(FloatingActionButton child) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.setMargins(0, 0, fabMarginPx, fabMarginPx);
        child.setLayoutParams(lp);
    }

    private void setFabMarginWithDependency(FloatingActionButton child, int dependencyHeight) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.setMargins(0, 0, fabMarginPx, screenHeightPx - dependencyHeight + fabMarginPx);
        child.setLayoutParams(lp);
    }
}
