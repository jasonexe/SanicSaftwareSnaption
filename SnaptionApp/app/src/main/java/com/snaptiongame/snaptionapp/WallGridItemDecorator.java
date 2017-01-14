package com.snaptiongame.snaptionapp;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * Created by brittanyberlanga on 1/12/17.
 */
public class WallGridItemDecorator extends RecyclerView.ItemDecoration {
    private int space;

    public WallGridItemDecorator(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams)view .getLayoutParams();
        int spanIndex = lp.getSpanIndex();
        if(spanIndex == 0){
            outRect.left = space;
            outRect.right = space / 2;
        } else{
            outRect.right = space;
            outRect.left = space / 2;
        }
        outRect.top = space;
    }
}