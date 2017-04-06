package com.snaptiongame.snaption.ui;

import android.content.res.Resources;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.snaptiongame.snaption.R;

/**
 * Created by brittanyberlanga on 3/8/17.
 */

public class MainFabBehavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private boolean snackbarVisible = false;
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
                child.setY(Math.round(location[1] - res.getDimensionPixelSize(R.dimen.fab_margin) -
                        child.getHeight() * 1.5));
            // else if the bottom navigation view is gone
            } else {
                child.setY(Math.round(location[1] - res.getDimensionPixelSize(R.dimen.fab_margin) -
                        child.getHeight() * 0.5));
            }
        }
        // if a snackbar is visible, set the position of the fab relative to the snackbar
        else if (dependency instanceof Snackbar.SnackbarLayout){
            int minPos = res.getDisplayMetrics().heightPixels -
                    res.getDimensionPixelSize(R.dimen.wall_bottom_navigation_height);
            // if the snackbar is above the bottom navigation view
            if (location[1] < minPos) {
                snackbarVisible = true;
                child.setY(Math.round(location[1] - res.getDimensionPixelSize(R.dimen.fab_margin) -
                        child.getHeight() * 1.5));
            }
            // else if the snackbar is at the same height or lower than the bottom navigation view
            else {
                snackbarVisible = false;
            }
        }
        return true;
    }
}
