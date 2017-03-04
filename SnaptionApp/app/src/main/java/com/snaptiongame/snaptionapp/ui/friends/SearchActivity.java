package com.snaptiongame.snaptionapp.ui.friends;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Hristo Stoytchev
 */

public class SearchActivity extends Activity {

    private static final String USER_NAME = "displayName";
    private static final String EMAIL = "email";

    private List<User> users = new ArrayList<User>();
    private FriendsListAdapter userListAdapter;

    @BindView(R.id.search_list)
    protected RecyclerView userViewList;

    @BindView(R.id.search_notice)
    protected TextView searchNotice;

    private Context context = this;
    private int count = 0;

    private ResourceListener<List<User>> listener = new ResourceListener<List<User>>() {
        @Override
        public void onData(List<User> userList) {
            users.addAll(userList);
            count++;
            if (count > 1) {
                displayUsers();
            }
        }

        @Override
        public Class getDataType() {
            return User.class;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        userViewList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            FirebaseResourceManager.retrieveUsersByName(query, USER_NAME, listener);
            FirebaseResourceManager.retrieveUsersByName(query, EMAIL, listener);
        }

    }

    private void displayUsers() {
        if (users.size() > 0) {
            Set<User> set = new TreeSet<>(users);
            searchNotice.setVisibility(View.GONE);
            userListAdapter = new FriendsListAdapter(new ArrayList<>(set));
            userViewList.setAdapter(userListAdapter);
        }
        else {
            searchNotice.setVisibility(View.VISIBLE);
            searchNotice.setText("Nothing Found");
        }
    }

}
