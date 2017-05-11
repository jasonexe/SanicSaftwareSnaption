package com.snaptiongame.snaption.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.google.firebase.auth.FirebaseUser;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.games.GameActivity;
import com.snaptiongame.snaption.ui.wall.WallGridItemDecorator;
import com.snaptiongame.snaption.ui.wall.WallViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_METADATA_PATH;

/**
 * Created by austinrobarts on 5/8/17.
 */
public class ProfileTabFragment extends Fragment {

    @BindView(R.id.profile_recycler_list)
    protected RecyclerView recyclerView;

    private Unbinder unbinder;

    private ProfileCaptionsAdapter captionsAdapter;
    private ProfileGamesAdapter gamesAdapter;
    private WallViewAdapter wallViewAdapter;

    private static final int CAPTION_TAB_PAGE = 1;
    private static final int GAME_TAB_PAGE = 0;

    private int page = 0;
    private User user = null;
    private static String PROFILE_FRAGMENT_PAGE = "profile_page";
    private ResourceListener gameListener = new ResourceListener<GameMetadata>() {
        @Override
        public void onData(GameMetadata data) {
            // filter out private games if needed
            //TODO get private games for user
            List<GameMetadata> gameList = new ArrayList();
            gameList.add(data);
            wallViewAdapter.addItems(gameList);
        }

        @Override
        public Class getDataType() {
            return GameMetadata.class;
        }
    };

    public static ProfileTabFragment newInstance(int page, User user) {
        Bundle args = new Bundle();
        args.putInt(PROFILE_FRAGMENT_PAGE, page);
        args.putSerializable("user", user);
        ProfileTabFragment fragment = new ProfileTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_tab_page, container, false);
        page = getArguments().getInt(PROFILE_FRAGMENT_PAGE);
        user = (User)getArguments().getSerializable("user");
        unbinder = ButterKnife.bind(this, view);
        if (page == CAPTION_TAB_PAGE) {
            //on the captions tab
            GridLayoutManager captionViewManager = new GridLayoutManager(view.getContext(), 3, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(captionViewManager);
            List<Caption> captions;
            if (user.getId().equals(FirebaseUserResourceManager.getUserId())) {
                //get public and private captions
                captions = user.getAllCaptions();
            }
            else {
                //get private games only
                captions = user.getAllPublicCaptions();
            }
            captionsAdapter = new ProfileCaptionsAdapter(captions);
            recyclerView.setAdapter(captionsAdapter);
        } else {
            //on the games tab
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));
            wallViewAdapter = new WallViewAdapter(new ArrayList<GameMetadata>(),
                    ProfileActivity.getProfileActivityCreator(getContext()));
            wallViewAdapter.setOnClickGamePhotoListener(new WallViewAdapter.OnClickGamePhotoListener() {
                @Override
                public void onClickGamePhoto(View view, GameMetadata game) {
                    // start game activity with shared image transition
                    Intent createGameIntent = new Intent(getActivity(), GameActivity.class);
                    createGameIntent.putExtra(Constants.GAME, game);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(getActivity(), view, game.getId());
                    getActivity().startActivity(createGameIntent, options.toBundle());
                }
            });
            getUserGames(user);
            recyclerView.setAdapter(wallViewAdapter);
        }

        return view;
    }

    private void getUserGames(User user) {
        Map<String, Integer> gameIds = user.getCreatedPublicGames();
        //if User has any games
        if (gameIds != null) {
            //for each gameId in user's game list
            for (String gameId : gameIds.keySet()) {
                FirebaseResourceManager.retrieveSingleNoUpdates(String.format(GAME_PUBLIC_METADATA_PATH, gameId), gameListener);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
