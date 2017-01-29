package com.snaptiongame.snaptionapp.ui.wall;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.captioner_text)
    public TextView captionerText;
    @BindView(R.id.photo)
    public ImageView photo;
    @BindView(R.id.caption_text)
    public TextView captionText;
    @BindView(R.id.caption_photo)
    public ImageView captionPhoto;
    @BindView(R.id.create_from_existing_button)
    public Button createFromExisting;

    public WallViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}