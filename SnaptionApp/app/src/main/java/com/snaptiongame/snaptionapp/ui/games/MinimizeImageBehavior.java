package com.snaptiongame.snaptionapp.ui.games;

import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaptiongame.snaptionapp.R;

import butterknife.ButterKnife;
/**
 * MinimizeImageBehavior represents the minimizing and expanding behavior of an image view that is
 * dependent on the position of a AppBarLayout above it. The image with collapse to half it's
 * original or specified max height.
 *
 * @author Brittany Berlanga
 */
public class MinimizeImageBehavior extends CoordinatorLayout.Behavior<ImageView> {
    private static final float MIN_PERCENT_HEIGHT = 0.5f;
    private static final int MAX_IMAGE_HEIGHT_DP = 400;
    private  float maxImageHeightPx;
    private static final String STATUS_BAR_HEIGHT_RES = "status_bar_height";
    private static final String DIMEN_RES = "dimen";
    private static final String ANDROID_RES = "android";
    private LinearLayout viewBelowImageView = null;
    private int appBarHeight = -1;
    private int statusBarHeight = -1;
    private int maxImageHeight = -1;


    public MinimizeImageBehavior() {}

    public MinimizeImageBehavior(LinearLayout viewBelowImageView) {
        this.viewBelowImageView = viewBelowImageView;
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child,
                                          View dependency) {
        if (appBarHeight < 0) {
            appBarHeight = dependency.getHeight();
            maxImageHeight = child.getHeight();
            Resources res = child.getResources();
            statusBarHeight = res.getDimensionPixelSize(res.getIdentifier(STATUS_BAR_HEIGHT_RES,
                    DIMEN_RES, ANDROID_RES));
            maxImageHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MAX_IMAGE_HEIGHT_DP, res.getDisplayMetrics());
        }

        int[] location = new int[2];
        dependency.getLocationOnScreen(location);
        float dependencyY = location[1];

        if (maxImageHeight <= 0) {
            maxImageHeight = child.getHeight();
            if (maxImageHeight > maxImageHeightPx) {
                maxImageHeight = Math.round(maxImageHeightPx);
            }
        }
        if (maxImageHeight > 0) {
            // translate the image view
            float imageY = dependencyY + appBarHeight - statusBarHeight;
            child.setY(imageY);
            // minimize/expand image view
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            int imageHeight = Math.round((maxImageHeight - maxImageHeight * MIN_PERCENT_HEIGHT) /
                    appBarHeight * (dependencyY - statusBarHeight) + maxImageHeight);
            lp.height = imageHeight;
            child.setLayoutParams(lp);

            if (viewBelowImageView != null) {
                // translate the view under the image view
                viewBelowImageView.setY(imageY + imageHeight);
                // changes the height of the view under the image view
                RecyclerView view = ButterKnife.findById(viewBelowImageView, R.id.recycler_caption_list);
                ((LinearLayout.LayoutParams)view.getLayoutParams()).setMargins(0, 0, 0, imageHeight);
            }
        }
        return true;
    }

    public void updateImageMaxHeight(int maxImageHeight) {
        this.maxImageHeight = maxImageHeight;
    }
}
