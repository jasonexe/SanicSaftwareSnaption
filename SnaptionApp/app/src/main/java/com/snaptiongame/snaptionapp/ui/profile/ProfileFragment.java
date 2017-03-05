package com.snaptiongame.snaptionapp.ui.profile;

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
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by austinrobarts on 1/23/17.
 */
public class ProfileFragment extends Fragment {

    private static final String GAME_DIRECTORY = "games";

    @BindView(R.id.profile_picture)
    public ImageView profile;
    @BindView(R.id.profile_name)
    public TextView userName;
    @BindView(R.id.games_created)
    public TextView gamesCreated;
    @BindView(R.id.captions_created)
    public TextView captionsCreated;
    @BindView(R.id.profile_games_list)
    protected RecyclerView gameListView;
    @BindView(R.id.profile_captions_list)
    protected RecyclerView captionsListView;

    private Unbinder unbinder;
    private ProfileGamesAdapter gameAdapter;
    private ProfileCaptionsAdapter captionsAdapter;
    private final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
    private ResourceListener gameListener = new ResourceListener<Game>() {
        @Override
        public void onData(Game data) {
            gameAdapter.addGame(data);
        }

        @Override
        public Class getDataType() {
            return Game.class;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        //set up all recycler view connections
        LinearLayoutManager gameViewManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        gameListView.setLayoutManager(gameViewManager);
        gameAdapter = new ProfileGamesAdapter(new ArrayList<Game>());
        gameListView.setAdapter(gameAdapter);

        //if the user is logged in
        if (FirebaseResourceManager.getUserPath() != null) {
            //retrieve information from User table
            FirebaseResourceManager.retrieveSingleNoUpdates(FirebaseResourceManager.getUserPath(), new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user.getDisplayName());
                    userName.setText(user.getDisplayName());
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(), profile);
                    gamesCreated.setText(Integer.toString(user.retrieveCreatedGameCount()));
                    captionsCreated.setText(Integer.toString(user.retrieveCaptionCount()));
                    //get the games based on list of games in user
                    getUserGames(user);
                    getUserCaptions(user, view);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
        return view;
    }

    private void getUserCaptions(User user, View view) {
        LinearLayoutManager captionViewManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        captionsListView.setLayoutManager(captionViewManager);
        Map<String, Caption> mapUserCaptions = user.getCaptions();
        // If the user has made any captions, display them, otherwise use an empty arraylist
        if(mapUserCaptions != null) {
            captionsAdapter = new ProfileCaptionsAdapter(new ArrayList<>(mapUserCaptions.values()));
        } else {
            captionsAdapter = new ProfileCaptionsAdapter(new ArrayList<Caption>());
        }
        captionsListView.setAdapter(captionsAdapter);
    }

    private void getUserGames(User user) {
        Map<String, Integer> gameIds = user.getCreatedGames();
        //if User has any games
        if (gameIds != null) {
            //for each gameId in user's game list
            for (String gameId : gameIds.keySet()) {
                FirebaseResourceManager.retrieveSingleNoUpdates(GAME_DIRECTORY + "/" + gameId, gameListener);
            }
        }
    }

    public void displayEditViews() {
        editDisplayName();
        editProfilePic();
    }

    private void editDisplayName() {

    }

    private void editProfilePic() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
