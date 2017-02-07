package com.snaptiongame.snaptionapp.ui.games;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by camgeehr on 2/6/17.
 */

public class GameViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.captioner_photo)
    protected ImageView captionerPhoto;
    @BindView(R.id.captioner_name)
    protected TextView captionerName;
    @BindView(R.id.upvote)
    protected ImageView upvote;
    @BindView(R.id.number_upvotes)
    protected TextView numberUpvotes;
    @BindView(R.id.caption_text)
    protected TextView captionText;

    public GameViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
