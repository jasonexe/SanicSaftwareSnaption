package com.snaptiongame.snaptionapp.ui.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 2/3/17.
 */

public class FriendsFragment extends Fragment {

    @BindView(R.id.friend_notice)
    public TextView friendNotice;
    @BindView(R.id.friend_list)
    protected RecyclerView friendsListView;

    private FriendsListAdapter friendsListAdapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager friendsViewManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendsListView.setLayoutManager(friendsViewManager);

        populateFriends();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.friends));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * Populates the friends list with your current snaption friends.
     */
    private void populateFriends() {
        if (FirebaseResourceManager.getUserPath() != null) {
            FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    if (user != null && user.getFriends() != null) {
                        friendNotice.setVisibility(View.GONE);
                        List<User> users = new ArrayList<>();
                        friendsListAdapter = new FriendsListAdapter(users);
                        friendsListView.setAdapter(friendsListAdapter);
                        loadUsers(user.getFriends());
                    }
                    else {
                        friendNotice.setVisibility(View.VISIBLE);
                        friendNotice.setText("You don't have any friends yet!\n\nClick on the button below to find someone you know.");
                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    private void loadUsers(List<String> uids) {
        for (String uid : uids) {
            // to avoid making another constant variable
            String friend = WallViewAdapter.USER_PATH + uid;

            // TODO change where the validFirebasePath method is called from
            // ensure the user id is a valid one to avoid errors
            if (WallViewAdapter.validFirebasePath(friend)) {
                // display the name and profile picture if a valid user is obtained from the user id
                FirebaseResourceManager.retrieveSingleNoUpdates(friend, new ResourceListener<User>() {
                    @Override
                    public void onData(User user) {
                        if (user != null) {
                            friendsListAdapter.addSingleItem(user);
                        }
                    }

                    @Override
                    public Class getDataType() {
                        return User.class;
                    }
                });
            }
        }
    }
}
