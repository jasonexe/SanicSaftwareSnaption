package com.snaptiongame.snaptionapp.ui.friends;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Hristo Stoytchev
 */

public class SearchActivity extends Activity {

    private List<User> users;
    private FriendsListAdapter userListAdapter;

    @BindView(R.id.search_list)
    protected RecyclerView userViewList;

    @BindView(R.id.search_notice)
    protected TextView searchNotice;

    private Context context = this;

    private ResourceListener<List<User>> listener = new ResourceListener<List<User>>() {
        @Override
        public void onData(List<User> userList) {
            users = userList;

            if (users != null) {
                searchNotice.setVisibility(View.GONE);
                userListAdapter = new FriendsListAdapter(users);
                userViewList.setAdapter(userListAdapter);
            }
            else {
                searchNotice.setVisibility(View.VISIBLE);
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
            FirebaseResourceManager.retrieveUsersByName(query, listener);

        }
    }

}
