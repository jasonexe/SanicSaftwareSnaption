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
    private boolean snackbarVisible;
    private boolean bottomNavVisible;
    private float bottomNavY;
    private int fabMarginPx;

    public MainFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        fabMarginPx = res.getDimensionPixelSize(R.dimen.fab_margin);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof BottomNavigationView || dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        if (dependency instanceof Snackbar.SnackbarLayout) {
            snackbarVisible = false;
        }
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        boolean fabPositionChanged = false;

        if (dependency instanceof BottomNavigationView) {
            if (!snackbarVisible && dependency.getVisibility() == View.VISIBLE) {
                // set the position of the fab relative to the bottom navigation view
                fabPositionChanged = setFabYWithDependencyY(child, dependency.getY());
            } else if (!snackbarVisible) {
                // set the position of the fab relative to the bottom of the parent view
                fabPositionChanged = setDefaultFabY(child, parent);
            }
            bottomNavY = dependency.getY();
            bottomNavVisible = dependency.getVisibility() == View.VISIBLE;
        }
        else if (dependency instanceof Snackbar.SnackbarLayout){
            // if the snackbar is above the bottom navigation view, set the position of the
            // fab relative to the snackbar
            if (!bottomNavVisible || dependency.getY() <= bottomNavY) {
                snackbarVisible = true;
                fabPositionChanged = setFabYWithDependencyY(child, dependency.getY());
            }
        }

        return fabPositionChanged;
    }

    private boolean setDefaultFabY(FloatingActionButton child, CoordinatorLayout parent) {
        return setFabY(child, parent.getHeight() - fabMarginPx - child.getHeight());
    }

    private boolean setFabYWithDependencyY(FloatingActionButton child, float dependencyY) {
        float calculatedY = dependencyY - fabMarginPx - child.getHeight();
        return setFabY(child, calculatedY);
    }

    /**
     * Sets the y-position of the floating action button to the given yPosition if the fab is
     * not already at that position
     * @param child the floating action button
     * @param yPosition the new y-position
     * @return whether the y-position of the floating action button was changed
     */
    private boolean setFabY(FloatingActionButton child, float yPosition) {
        boolean yChanged = false;
        if (child.getY() != yPosition) {
            child.setY(yPosition);
            yChanged = true;
        }
        return yChanged;
    }
}
