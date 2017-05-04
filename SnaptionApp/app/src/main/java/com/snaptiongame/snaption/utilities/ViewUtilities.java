package com.snaptiongame.snaption.utilities;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by brittanyberlanga on 2/19/17.
 */

public class ViewUtilities {
    private static float COLLAPSE_ALPHA = 0.0f;
    private static float EXPAND_ALPHA = 1.0f;
    /**
     * Expands or collapses the given view by increasing/reducing height of the view and fading
     * in/out the view.
     *
     * @param view View to be animated
     * @param minHeight minimum height of the view when collapsed
     * @param maxHeight maximum height of the view when expanded
     * @param duration duration of the animation
     */
    public static void expandCollapseView(final View view, final int minHeight, final int maxHeight, final long duration) {
        final float maxHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxHeight,
                view.getResources().getDisplayMetrics());
        final float minHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeight,
                view.getResources().getDisplayMetrics());

        // if the height is the minHeight, expand the view
        final boolean expand = view.getLayoutParams().height == (int) minHeightPx;
        float startAlpha;
        float endAlpha;
        final int endHeight;

        if (expand) {
            startAlpha = COLLAPSE_ALPHA;
            endAlpha = EXPAND_ALPHA;
            endHeight = (int) maxHeightPx;
            view.setVisibility(View.VISIBLE);
        }
        else {
            startAlpha = EXPAND_ALPHA;
            endAlpha = COLLAPSE_ALPHA;
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

    /**
     * Creates a "ghost" image of a given ImageView. The ghost will originate at the ImageView's
     * position, animate upwards, and then disappear. During the animation, the ghost will scale
     * it's size and become transparent.
     *
     * @param parent ViewGroup containing the ImageView and the ViewGroup the ghost will be added to
     * @param imageView ImageView to create a ghost of
     * @param distanceDp upward distance the ghost should travel
     * @param duration duration of the animation
     * @param scaleBy scale amount
     */
    public static void animateGhost(final ViewGroup parent, ImageView imageView, float distanceDp,
                                    int duration, float scaleBy) {
        final ImageView ghostView = new ImageView(imageView.getContext());
        ghostView.setImageDrawable(imageView.getDrawable());
        parent.addView(ghostView);
        ghostView.setX(imageView.getX());
        ghostView.setY(imageView.getY());
        float animationDist = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, distanceDp,
                parent.getResources().getDisplayMetrics());
        ghostView.animate().alpha(0f)
                .translationYBy(-animationDist).setDuration(duration).scaleXBy(scaleBy)
                .scaleYBy(scaleBy).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        parent.removeView(ghostView);
                    }
                    @Override
                    public void onAnimationStart(Animator animation) {}
                    @Override
                    public void onAnimationCancel(Animator animation) {}
                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                }).start();
    }

    /**
     * Calculates the height of a view based on the aspect ratio, max view width, and max view
     * height. The height is calculated with respect to the max width. If the calculated height is
     * greater than the max height, the max height is returned.
     *
     * @param aspectRatio view width / view height
     * @param maxWidthPx maximum width of the view
     * @param maxHeightPx maximum height of the view
     * @return calculated height of the view
     */
    public static int calculateViewHeight(double aspectRatio, double maxWidthPx,
                                          double maxHeightPx) {
        int imageHeight = (int) (maxWidthPx / aspectRatio);
        if (imageHeight > maxHeightPx) {
            imageHeight = (int) maxHeightPx;
        }
        return imageHeight;
    }

    /**
     * Calculates the width of a view based on the aspect ratio, max view width, and max view
     * height. The width is calculated with respect to the max height. If the calculated width is
     * greater than the max width, the max width is returned.
     *
     * @param aspectRatio view width / view height
     * @param maxWidthPx maximum width of the view
     * @param maxHeightPx maximum height of the view
     * @return calculated width of the view
     */
    public static int calculateViewWidth(double aspectRatio, double maxWidthPx,
                                          double maxHeightPx) {
        int imageWidth = (int) (maxHeightPx * aspectRatio);
        if (imageWidth > maxWidthPx) {
            imageWidth = (int) maxWidthPx;
        }
        return imageWidth;
    }
}
