package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Hristo Stoytchev
 */

public class FriendsListHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.friend_username)
    public TextView friendName;
    @BindView(R.id.friend_picture)
    public ImageView friendPicture;

    public FriendsListHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
