package com.snaptiongame.snaptionapp.ui.friends;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FriendViewHolder is a RecyclerView.ViewHolder used to hold a simple friend view displaying a
 * friend's name and photo.
 *
 * @author Brittany Berlanga
 */
public class FriendViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.friend_name)
    TextView friendName;
    @BindView(R.id.friend_photo)
    ImageView friendPhoto;
    @BindView(R.id.add_invite_button)
    Button addInviteButton;
    @BindView(R.id.friend_email)
    TextView friendEmail;

    public static FriendViewHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_friend_item,
                parent, false);
        return new FriendViewHolder(view);
    }


    private FriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
