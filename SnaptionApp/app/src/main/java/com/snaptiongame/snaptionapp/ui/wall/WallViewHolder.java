package com.snaptiongame.snaptionapp.ui.wall;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.picker_name)
    public TextView pickerName;
    @BindView(R.id.photo)
    public ImageView photo;
    @BindView(R.id.caption_text)
    public TextView captionText;
    @BindView(R.id.captioner_container)
    public LinearLayout captionerLayout;
    @BindView(R.id.captioner_name)
    public TextView captionerName;
    @BindView(R.id.captioner_photo)
    public ImageView captionerPhoto;
    @BindView(R.id.upvote_icon)
    public ImageView upvoteIcon;
    @BindView(R.id.upvote_count)
    public TextView upvoteCountText;


    public WallViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}