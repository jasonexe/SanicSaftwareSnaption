package com.snaptiongame.snaptionapp.ui.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.ScrollFabHider;
import com.snaptiongame.snaptionapp.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        //set up fab scroll listener
        FloatingActionButton fab = (FloatingActionButton)this.getActivity().findViewById(R.id.fab);
        ScrollFabHider scrollFabHider = new ScrollFabHider(fab, ScrollFabHider.BIG_HIDE_THRESHOLD);
        friendsListView.addOnScrollListener(scrollFabHider);

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
                        friendsListAdapter = new FriendsListAdapter(users, ProfileActivity.getProfileActivityCreator(getContext()));
                        friendsListView.setAdapter(friendsListAdapter);
                        loadUsers(user.getFriends());
                    }
                    else {
                        friendNotice.setVisibility(View.VISIBLE);
                        friendNotice.setText(R.string.empty_friends);
                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    private void loadUsers(Map<String, Integer> uids) {
        FirebaseResourceManager.loadUsers(uids, new ResourceListener<User>() {
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
