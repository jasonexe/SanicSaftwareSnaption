package com.snaptiongame.snaption.ui.user;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by brittanyberlanga on 5/10/17.
 */

class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    protected TextView name;
    @BindView(R.id.email)
    protected TextView email;
    @BindView(R.id.photo)
    protected ImageView photo;
    @BindView(R.id.action_button)
    protected TextView actionButton;

    private UserMetadata user;
    private ActionListener actionListener;

    interface ActionListener {
        void onClickItem(UserMetadata user);
        void onClickAction(UserMetadata user);
        String actionText(UserMetadata user);
    }

    public static UserViewHolder newInstance(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_item,
                parent, false);
        return new UserViewHolder(view);
    }

    private UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(UserMetadata user) {
        this.user = user;
        if (user != null) {
            name.setText(user.getDisplayName());
            email.setText(user.getEmail());
            email.setVisibility(TextUtils.isEmpty(user.getEmail()) ? View.GONE : View.VISIBLE);
            FirebaseResourceManager.loadImageIntoView(user.getImagePath(), photo);
            actionButton.setText(actionListener != null ? actionListener.actionText(user) : null);
            actionButton.setVisibility(actionListener != null ? View.VISIBLE : View.GONE);
        }
        else {
            name.setText(null);
            email.setText(null);
            photo.setImageDrawable(null);
            actionButton.setText(null);
            actionButton.setVisibility(View.GONE);
        }
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    @OnClick(R.id.item_view)
    public void onClickItem() {
        if (actionListener != null && user != null) {
            actionListener.onClickItem(user);
        }
    }

    @OnClick(R.id.action_button)
    public void onClickActionButton() {
        if (actionListener != null && user != null) {
            actionListener.onClickAction(user);
        }
    }
}
