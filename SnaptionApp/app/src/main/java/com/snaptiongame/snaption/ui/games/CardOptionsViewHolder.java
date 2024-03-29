package com.snaptiongame.snaption.ui.games;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaption.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is just the view, make sure to bind stuff that you want to be able to reference
 * Created by jason_000 on 2/6/2017.
 */

public class CardOptionsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.possible_caption_card_text)
    TextView possibleCardView;
    @BindView(R.id.refresh_icon)
    ImageView refreshIcon;

    public CardOptionsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
