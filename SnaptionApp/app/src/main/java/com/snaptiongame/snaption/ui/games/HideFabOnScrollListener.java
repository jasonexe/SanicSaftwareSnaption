package com.snaptiongame.snaption.ui.games;

import android.support.design.widget.FloatingActionButton;

/**
 * Created by brittanyberlanga on 5/7/17.
 */

public class HideFabOnScrollListener implements MinimizeViewBehavior.OnScrollListener {
    private static final int SCROLL_LIMIT = 30;

    private FloatingActionButton fab;
    private int scrollChange;

    public HideFabOnScrollListener(FloatingActionButton fab) {
        this.fab = fab;
    }

    @Override
    public void onScroll(int dy) {
        // hide/show the fab when the scroll limit has been reached
        scrollChange += dy;
        if (scrollChange > SCROLL_LIMIT) {
            scrollChange = 0;
            fab.hide();
        }
        else if (scrollChange < -SCROLL_LIMIT) {
            scrollChange = 0;
            fab.show();
        }
    }
}
