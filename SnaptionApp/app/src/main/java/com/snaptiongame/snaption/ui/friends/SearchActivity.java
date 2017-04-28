package com.snaptiongame.snaption.ui.friends;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An Activity that displays the list of users obtained from a search.
 * Brings up a list of Snaption users based on what was typed into the search bar,
 * checking both the user's username and email, and allows you to add them as a friend.
 *
 * @author Hristo Stoytchev
 */
public class SearchActivity extends HomeAppCompatActivity {
    private List<UserMetadata> users = new ArrayList<UserMetadata>();
    private FriendsListAdapter userListAdapter;
    private FriendsViewModel viewModel;
    private FirebaseUploader uploader;
    private String query;

    @BindView(R.id.search_list)
    protected RecyclerView userViewList;

    @BindView(R.id.search_notice)
    protected TextView searchNotice;

    // the listener that gets the list of Users based on username
    private ResourceListener<List<UserMetadata>> nameListener = new ResourceListener<List<UserMetadata>>() {
        @Override
        public void onData(List<UserMetadata> userList) {
            users.addAll(userList);
            // query Firebase for Users based on e-mail only after this query is finished
            FirebaseUserResourceManager.getUserMetadataByName(query.toLowerCase(), Constants.EMAIL, emailListener);
        }

        @Override
        public Class getDataType() {
            return UserMetadata.class;
        }
    };

    // the listener that gets the list of Users based on e-mail
    private ResourceListener<List<UserMetadata>> emailListener = new ResourceListener<List<UserMetadata>>() {
        @Override
        public void onData(List<UserMetadata> userList) {
            users.addAll(userList);
            // display the list of Users after getting the remaining ones
            displayUsers();
        }

        @Override
        public Class getDataType() {
            return UserMetadata.class;
        }
    };

    FriendsListAdapter.AddInviteUserCallback addInviteUserCallback = new FriendsListAdapter.AddInviteUserCallback() {
        @Override
        public void addInviteClicked(final UserMetadata user) {
            final Friend friend = new Friend(user);

            // ensure viewModel has been initialized
            if (viewModel != null) {
                viewModel.addFriend(friend, new Uploader.UploadListener() {
                    @Override
                    public void onComplete() {
                        // notify user
                        Toast.makeText(SearchActivity.this,
                                viewModel.getAddedFriendText(SearchActivity.this,
                                        friend.displayName, true, null), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // notify user
                        Toast.makeText(SearchActivity.this,
                                viewModel.getAddedFriendText(SearchActivity.this,
                                        friend.displayName, false, errorMessage), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);
        // make sure that the search notice starts out invisible
        searchNotice.setVisibility(View.GONE);

        uploader = new FirebaseUploader();
        initializeViewModel();
        userViewList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        handleIntent(getIntent());
    }

    /**
     * Starts the search based on what was typed into the search bar.
     * @param intent Intent that initializes a search.
     */
    private void handleIntent(Intent intent) {

        // get the requested string if the action performed was to search
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            // query firebase for Users based on displayname
            FirebaseUserResourceManager.getUserMetadataByName(query.toLowerCase(), Constants.SEARCH_NAME, nameListener);
        }

    }

    /**
     * Displays the Users obtained from getting the search in alphabetical order,
     * showing both their username and email along with a button to add them as a friend.
     */
    private void displayUsers() {
        // display the list of users if there are any, otherwise tell the user nothing matched
        if (users.size() > 0) {
            // convert to a treeset to remove duplicates and be in alphabetical order
            Set<UserMetadata> set = new TreeSet<>(users);
            searchNotice.setVisibility(View.GONE);
            // set the adapter to be able to add friends
            userListAdapter = new FriendsListAdapter(new ArrayList<>(set), addInviteUserCallback, ProfileActivity.getProfileActivityCreator(this));
            userViewList.setAdapter(userListAdapter);
        }
        else {
            searchNotice.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initialize the view model that allows for adding friends.
     */
    private void initializeViewModel() {
        FirebaseUserResourceManager.getUserMetadataById(FirebaseUserResourceManager.getUserId(),
                new ResourceListener<UserMetadata>() {
                    @Override
                    public void onData(UserMetadata user) {
                        if (user != null) {
                            viewModel = new FriendsViewModel(user, uploader);
                        }
                    }

                    @Override
                    public Class getDataType() {
                        return UserMetadata.class;
                    }
                });
    }

}
