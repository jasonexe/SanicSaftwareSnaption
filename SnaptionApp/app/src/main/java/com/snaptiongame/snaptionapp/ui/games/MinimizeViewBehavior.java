package com.snaptiongame.snaptionapp.ui.games;

import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * MinimizeViewBehavior represents the minimizing and expanding behavior of an view that is
 * dependent on the position of a AppBarLayout above it. The view will collapse to half it's
 * original or specified max height. The maximum height of the view is 400dp.
 *
 * @author Brittany Berlanga
 */
public class MinimizeViewBehavior extends CoordinatorLayout.Behavior<View> {
    private static final float MIN_PERCENT_HEIGHT = 0.5f;
    private static final int MAX_VIEW_HEIGHT_DP = 350;
    private  float maxViewHeightPx;
    private int maxViewHeight = -1;
    private static final String STATUS_BAR_HEIGHT_RES = "status_bar_height";
    private static final String DIMEN_RES = "dimen";
    private static final String ANDROID_RES = "android";
    private View minimizeView;
    private int appBarHeight = -1;
    private int statusBarHeight = -1;



    public MinimizeViewBehavior() {}

    public MinimizeViewBehavior(View minimizeView) {
        this.minimizeView = minimizeView;
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                          View dependency) {
        if (appBarHeight < 0) {
            appBarHeight = dependency.getHeight();
            Resources res = child.getResources();
            statusBarHeight = res.getDimensionPixelSize(res.getIdentifier(STATUS_BAR_HEIGHT_RES,
                    DIMEN_RES, ANDROID_RES));
            maxViewHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    MAX_VIEW_HEIGHT_DP, res.getDisplayMetrics());
        }

        int[] location = new int[2];
        dependency.getLocationOnScreen(location);
        float dependencyY = location[1];

        if (maxViewHeight <= 0 || minimizeView.getHeight() > maxViewHeightPx) {
            updateViewMaxHeight(minimizeView.getHeight());
        }
        if (maxViewHeight > 0) {
            // minimize/expand view
            ViewGroup.LayoutParams lp = minimizeView.getLayoutParams();
            lp.height = Math.round((maxViewHeight - maxViewHeight * MIN_PERCENT_HEIGHT) /
                    appBarHeight * (dependencyY - statusBarHeight) + maxViewHeight);
            minimizeView.setLayoutParams(lp);
        }
        child.invalidate();
        return true;
    }

    public void updateViewMaxHeight(int maxViewHeight) {
        if (maxViewHeight > maxViewHeightPx) {
            this.maxViewHeight = Math.round(maxViewHeightPx);
        }
        else {
            this.maxViewHeight = maxViewHeight;
        }
    }
}