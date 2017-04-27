package com.snaptiongame.snaption.ui.friends;

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

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.ScrollFabHider;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

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

    private FirebaseResourceManager firebase = new FirebaseResourceManager();
    private FirebaseUserResourceManager userFirebase = new FirebaseUserResourceManager();
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
    public void onResume() {
        super.onResume();
        populateFriends();
    }

    @Override
    public void onPause() {
        super.onPause();
        firebase.removeListener();
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
        final String userId = FirebaseUserResourceManager.getUserId();
        if (FirebaseUserResourceManager.getUserId() != null) {
            userFirebase.getUserFriendsWithUpdates(userId, new ResourceListener<Map<String, Integer>>() {
                @Override
                public void onData(Map<String, Integer> userIds) {
                    if (userIds != null) {
                        friendNotice.setVisibility(View.GONE);
                        List<UserMetadata> users = new ArrayList<>();
                        friendsListAdapter = new FriendsListAdapter(users, ProfileActivity.getProfileActivityCreator(getContext()));
                        friendsListView.setAdapter(friendsListAdapter);
                        loadUsers(userIds);
                    }
                    else {
                        friendNotice.setVisibility(View.VISIBLE);
                        friendNotice.setText(R.string.empty_friends);
                    }
                }

                @Override
                public Class getDataType() {
                    return Map.class;
                }
            });
        }
    }

    private void loadUsers(Map<String, Integer> uids) {
        FirebaseUserResourceManager.getUsersMetadataByIds(uids, new ResourceListener<UserMetadata>() {
            @Override
            public void onData(UserMetadata user) {
                if (user != null) {
                    friendsListAdapter.addSingleItem(user);
                }
            }

            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        });
    }
}
