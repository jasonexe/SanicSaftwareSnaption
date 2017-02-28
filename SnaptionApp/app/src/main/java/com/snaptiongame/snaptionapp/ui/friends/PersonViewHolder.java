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
 * PersonViewHolder is a RecyclerView.ViewHolder used to hold a simple person view displaying a
 * person's name, email, photo, and an add/invite button.
 *
 * @author Brittany Berlanga
 */
public class PersonViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.email)
    public TextView email;
    @BindView(R.id.photo)
    public ImageView photo;
    @BindView(R.id.add_invite_button)
    public Button addInviteButton;

    public static PersonViewHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_person_item,
                parent, false);
        return new PersonViewHolder(view);
    }

    private PersonViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
