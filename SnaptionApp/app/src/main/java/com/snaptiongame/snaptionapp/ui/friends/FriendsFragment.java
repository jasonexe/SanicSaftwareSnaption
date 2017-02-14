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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 2/3/17.
 */

public class FriendsFragment extends Fragment {
    @BindView(R.id.friend_list)
    protected RecyclerView friendsListView;

    private final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
    private FriendsListAdapter friendsAdapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);
        LinearLayoutManager friendsViewManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        friendsListView.setLayoutManager(friendsViewManager);

        if (FirebaseResourceManager.getUserPath() != null) {
            firebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    if (user != null && user.getFriends() != null) {
                        FriendsListAdapter adapter = new FriendsListAdapter(user.getFriends());
                        friendsListView.setAdapter(adapter);
                    }
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.friends));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
