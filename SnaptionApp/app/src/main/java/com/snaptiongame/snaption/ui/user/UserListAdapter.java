package com.snaptiongame.snaption.ui.user;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author brittany.berlanga
 */
public abstract class UserListAdapter extends RecyclerView.Adapter<UserViewHolder>
        implements UserViewHolder.ActionListener {
    private List<String> userIds;
    private Map<String, UserMetadata> userMap;
    private ProfileActivity.ProfileActivityCreator profileMaker;

    public UserListAdapter(List<String> userIds,
                           ProfileActivity.ProfileActivityCreator profileMaker) {
        this.userIds = userIds;
        this.userMap = new HashMap<>();
        this.profileMaker = profileMaker;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserViewHolder userViewHolder = UserViewHolder.newInstance(parent);
        userViewHolder.setActionListener(this);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        final UserMetadata userMetadata = userMap.get(userIds.get(position));
        holder.bind(userMetadata);
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    void setUser(UserMetadata user) {
        if (userIds.contains(user.getId())) {
            userMap.put(user.getId(), user);
            notifyItemChanged(userIds.indexOf(user.getId()));
        }
    }

    @Override
    public void onClickItem(UserMetadata user) {
        profileMaker.create(user.getId());
    }

    @Override
    public abstract void onClickAction(UserMetadata user);

    @Override
    public abstract String actionText(UserMetadata user);
}
