package com.snaptiongame.snaption.ui.games;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by brittanyberlanga on 4/29/17.
 */

public class NonClickableToolbar extends Toolbar {
    public NonClickableToolbar(Context context) {
        super(context);
    }

    public NonClickableToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NonClickableToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
