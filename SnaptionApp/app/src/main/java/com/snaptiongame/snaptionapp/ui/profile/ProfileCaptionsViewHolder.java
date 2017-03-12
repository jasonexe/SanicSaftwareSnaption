package com.snaptiongame.snaptionapp.ui.profile;

import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason Krein on 2/12/2017.
 */

public class ProfileCaptionsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.possible_caption_card_text)
    public TextView captionText;
    @BindView(R.id.caption_upvotes)
    public TextView captionUpvotes;


    public ProfileCaptionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
