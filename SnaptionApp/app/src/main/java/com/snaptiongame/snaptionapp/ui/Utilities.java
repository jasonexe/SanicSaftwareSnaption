package com.snaptiongame.snaptionapp.ui;

import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by brittanyberlanga on 2/19/17.
 */

public class Utilities {
    public static void expandCollapseView(final View view, final int minHeight, final int maxHeight, final long duration) {
        final float maxHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxHeight,
                view.getResources().getDisplayMetrics());
        final float minHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeight,
                view.getResources().getDisplayMetrics());

        final boolean expand = view.getLayoutParams().height == (int) minHeightPx;
        float startAlpha;
        float endAlpha;
        final int endHeight;

        // if the height is the minHeight, expand the view
        if (expand) {
            startAlpha = 0.0f;
            endAlpha = 1.0f;
            endHeight = (int) maxHeightPx;
            view.setVisibility(View.VISIBLE);
        }
        else {
            startAlpha = 1.0f;
            endAlpha = 0.0f;
            endHeight = (int) minHeightPx;
        }
        view.setAlpha(startAlpha);
        view.animate().alpha(endAlpha).setDuration(duration);
        ValueAnimator heightAnimator = ValueAnimator.ofFloat(0);
        heightAnimator.setDuration(duration).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long currentDuration = animation.getCurrentPlayTime();
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (expand) {
                    lp.height = Math.round(currentDuration / (float) duration * maxHeightPx);
                }
                else {
                    lp.height = Math.round((1 - currentDuration / (float)duration) * maxHeightPx);
                }
                if (currentDuration >= duration) {
                    lp.height = endHeight;
                    if (!expand) {
                        view.setVisibility(View.GONE);
                    }
                }
                view.setLayoutParams(lp);
            }
        });
        heightAnimator.start();
    }
}
