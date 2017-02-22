package com.snaptiongame.snaptionapp.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by austinrobarts on 2/21/17.
 */

public class ScrollViewHider extends RecyclerView.OnScrollListener {
    public final static int BIG_HIDE_THRESHOLD = 10;
    public final static int HALF_HIDE_THRESHOLD = 5;
    private int scrolledDistance;
    private boolean fabVisible; // This is used because the animations take too long
    private FloatingActionButton fab;
    private int hide_threshold;

    public ScrollViewHider(FloatingActionButton view, int hide_threshold) {
        this.fab = view;
        this.hide_threshold = hide_threshold;
        scrolledDistance = 0;
        fabVisible = true;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // If they've scrolled more than HIDE_THRESHOLD units, and fab is visible, hide it
        // If they've scrolled less than the units (means they goin up)
        // and fab isn't visible, show it
        if (scrolledDistance > hide_threshold && isFabVisible()) {
            fabVisible = false;
            fab.hide();
        } else if (scrolledDistance < -hide_threshold && !isFabVisible()) {
            fabVisible = true;
            fab.show();
        }

        // If the user scrolls in a direction that would change the fab visibility, increment
        // the distance. If they've already hidden the fab and are still scrolling down,
        // for example, don't increment the distance. But if they are scrolling down while
        // fab is showing, increment it.
        if((isFabVisible() && dy > 0) || !isFabVisible() && dy < 0 || isFabVisible() && dx > 0
                || !isFabVisible() && dx < 0){
            scrolledDistance += dy + dx;
        }
    }

    private boolean isFabVisible() {
        return fabVisible;
    }


}
