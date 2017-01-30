package com.snaptiongame.snaptionapp.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseGameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.GameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.profile.ProfileGamesAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by austinrobarts on 1/23/17.
 */
public class ProfileFragment extends Fragment {


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

    private ProfileGamesAdapter gameAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager gameViewManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        final FirebaseResourceManager firebaseResourceManager = new FirebaseResourceManager();
        gameListView.setLayoutManager(gameViewManager);
        gameAdapter = new ProfileGamesAdapter(new ArrayList<Game>());
        gameListView.setAdapter(gameAdapter);

        //if the user is logged in
        if (FirebaseResourceManager.getUserPath() != null) {
            //retrieve information from User table
            firebaseResourceManager.retrieveSingleWithUpdates(firebaseResourceManager.getUserPath(), new ResourceListener() {
                @Override
                public void onData(Object data) {
                    if (data instanceof User)
                    {
                        User user = (User)data;
                        userName.setText(user.getDisplayName());
                        FirebaseResourceManager.loadProfilePictureIntoView(user.getImagePath(), profile);
                        gamesCreated.setText(Integer.toString(user.retrieveGameCount()));
                        captionsCreated.setText(Integer.toString(user.retrieveCaptionCount()));
                        //get the games based on list of games in user
                        getUserGames(user);

                    }
                    //close the listener
                    firebaseResourceManager.removeListener();
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
        return view;
    }

    private void getUserGames(User user) {

        List<String> gameIds = user.getGames();

        if (gameIds != null) {
            ResourceListener gameListener = new ResourceListener() {
                @Override
                public void onData(Object data) {
                    if (data instanceof Game) {
                        gameAdapter.addGame((Game)data);
                    }
                }

                @Override
                public Class getDataType() {
                    return Game.class;
                }
            };
            //get resourceManager to get games
            GameResourceManager resourceManager = new FirebaseGameResourceManager(0, null);
            for (String gameId : gameIds) {
                resourceManager.retrieveGameById(gameId, gameListener);
            }
        }


    }
}
