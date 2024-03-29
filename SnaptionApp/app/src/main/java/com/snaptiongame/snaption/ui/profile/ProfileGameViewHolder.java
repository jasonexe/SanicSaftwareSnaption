package com.snaptiongame.snaption.ui.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.snaptiongame.snaption.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by austinrobarts on 1/29/17.
 */

public class ProfileGameViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.photo)
    public ImageView photo;

    /* Data fields needed if we are going to show captions
    @BindView(R.id.caption_text)
    public TextView captionText;
    @BindView(R.id.caption_photo)
    public ImageView captionPhoto;
    @BindView(R.id.captioner_text)
    public TextView captionerName; */

    public ProfileGameViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
