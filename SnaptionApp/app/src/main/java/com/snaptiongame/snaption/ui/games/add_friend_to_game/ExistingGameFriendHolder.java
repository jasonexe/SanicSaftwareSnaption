package com.snaptiongame.snaption.ui.games.add_friend_to_game;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.ui.friends.PersonViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jason Krein on 5/6/2017.
 */

public class ExistingGameFriendHolder extends RecyclerView.ViewHolder  {
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.email)
    public TextView email;
    @BindView(R.id.photo)
    public ImageView photo;
    @BindView(R.id.add_invite_button)
    public Button addInviteButton;

    public static ExistingGameFriendHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_person_item,
                parent, false);
        return new ExistingGameFriendHolder(view);
    }

    private ExistingGameFriendHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
