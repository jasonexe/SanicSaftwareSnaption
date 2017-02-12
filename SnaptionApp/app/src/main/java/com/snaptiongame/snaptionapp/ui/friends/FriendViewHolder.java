package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

/**
 * FriendViewHolder is a RecyclerView.ViewHolder used to hold a simple friend view displaying a
 * friend's name and photo.
 *
 * @author Brittany Berlanga
 */
public class FriendViewHolder extends RecyclerView.ViewHolder {
    public final TextView friendName;
    public final ImageView friendPhoto;
    public final Button addInviteButton;


    public FriendViewHolder(View itemView) {
        super(itemView);
        friendName = (TextView) this.itemView.findViewById(R.id.friend_name);
        friendPhoto = (ImageView) this.itemView.findViewById(R.id.friend_photo);
        addInviteButton = (Button) this.itemView.findViewById(R.id.add_invite_button);
    }
}
