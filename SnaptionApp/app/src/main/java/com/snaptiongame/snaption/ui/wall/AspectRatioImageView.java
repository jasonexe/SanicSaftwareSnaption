package com.snaptiongame.snaption.ui.wall;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.snaptiongame.snaption.utilities.ViewUtilities;

/**
 * Created by brittanyberlanga on 4/30/17.
 */

public class AspectRatioImageView extends AppCompatImageView {
    private double maxImageHeight = Double.MAX_VALUE;
    private double aspectRatio;

    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (aspectRatio != 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = ViewUtilities.calculateViewHeight(aspectRatio, width, maxImageHeight);
            if (height == maxImageHeight) {
                width = ViewUtilities.calculateViewWidth(aspectRatio, width, height);
            }
            setMeasuredDimension(width, height);
        }
    }

    public void setImageAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public void setMaxImageHeight(double maxImageHeight) {
        this.maxImageHeight = maxImageHeight;
    }
}
