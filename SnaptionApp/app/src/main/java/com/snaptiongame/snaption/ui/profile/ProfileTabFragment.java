package com.snaptiongame.snaption.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.wall.WallGridItemDecorator;
import com.snaptiongame.snaption.ui.wall.WallViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_METADATA_PATH;
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
    private static final int NUM_COLUMNS = 2;

    private int page = 0;
    private User user = null;
    private static final String PROFILE_FRAGMENT_PAGE = "profile_page";
    private static final String USER = "user";
    private List<FirebaseResourceManager> gameVoteListeners;
    private ResourceListener gameListener = new ResourceListener<GameMetadata>() {
        @Override
        public void onData(GameMetadata data) {
            List<GameMetadata> gameList = new ArrayList<>();
            gameList.add(data);
            ResourceListener<Map<String, Integer>> listener =
                    getUpdatingListener(gameVoteListeners.size());
            FirebaseResourceManager manager = new FirebaseResourceManager();
            String upvotesPath = data.getIsPublic() ?
                    String.format(Constants.GAME_PUBLIC_METADATA_UPVOTES_PATH,
                            data.getId()) :
                    String.format(Constants.GAME_PRIVATE_METADATA_UPVOTES_PATH,
                            data.getId());
            manager.retrieveMapWithUpdates(upvotesPath, listener);
            gameVoteListeners.add(manager);
            wallViewAdapter.addItems(gameList);
        }

        @Override
        public Class getDataType() {
            return GameMetadata.class;
        }
    };

    private ResourceListener<Map<String, Integer>> getUpdatingListener(final int gameNum) {
        return new ResourceListener<Map<String, Integer>>() {
            @Override
            public void onData(Map<String, Integer> data) {
                if (isVisible() || !isRemoving()) {
                    wallViewAdapter.gameChanged(gameNum, data);
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        };
    }

    public static ProfileTabFragment newInstance(int page, User user) {
        Bundle args = new Bundle();
        args.putInt(PROFILE_FRAGMENT_PAGE, page);
        args.putSerializable(USER, user);
        ProfileTabFragment fragment = new ProfileTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_tab_page, container, false);
        page = getArguments().getInt(PROFILE_FRAGMENT_PAGE);
        user = (User)getArguments().getSerializable(USER);
        unbinder = ButterKnife.bind(this, view);
        gameVoteListeners = new ArrayList<>();
        if (page == CAPTION_TAB_PAGE) {
            //on the captions tab
            setCapationRecyclerView();
        } else {
            //on the games tab
            //made to have same layout as wall
            setGameRecyclerView();
        }

        return view;
    }

    private void setGameRecyclerView() {
        //on the games tab
        //made to have same layout as wall
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));
        wallViewAdapter = new WallViewAdapter(new ArrayList<GameMetadata>());
        getUserGames(user);
        recyclerView.setAdapter(wallViewAdapter);
    }

    private void setCapationRecyclerView() {
        //on the captions tab
        int captionWidth = (int) getResources().getDimension(R.dimen.caption_card_width);
        int phoneWidth = getResources().getDisplayMetrics().widthPixels;
        //based on phone width and caption width find how many can fit
        int numCaptionColumns = phoneWidth / captionWidth;
        GridLayoutManager captionViewManager = new GridLayoutManager(getContext(), numCaptionColumns, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(captionViewManager);

        captionsAdapter = new ProfileCaptionsAdapter(getUserCaptions());
        recyclerView.setAdapter(captionsAdapter);
    }

    private List<Caption> getUserCaptions() {
        List<Caption> captions = new ArrayList<>();
        //if user is looking at their own profile
        if (user.getId().equals(FirebaseUserResourceManager.getUserId())) {
            //get public and private captions
            captions.addAll(user.getAllPrivateCaptions());
        }
        //add public games
        captions.addAll(user.getAllPublicCaptions());
        Collections.sort(captions);
        return captions;
    }

    private void getUserGames(User user) {
        Map<String, Integer> publicGameIds = user.getCreatedPublicGames();
        Map<String, Integer> privateGameIds = user.getCreatedPrivateGames();
        //if User has any games
        if (publicGameIds != null) {
            //for each gameId in user's public game list
            for (String gameId : publicGameIds.keySet()) {
                FirebaseResourceManager.retrieveSingleNoUpdates(String.format(GAME_PUBLIC_METADATA_PATH, gameId), gameListener);
            }
        }
        if (privateGameIds != null) {
            //for each gameId in user's private game list
            for (String gameId : privateGameIds.keySet()) {
                FirebaseResourceManager.retrieveSingleNoUpdates(String.format(GAME_PRIVATE_METADATA_PATH, gameId), gameListener);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearListeners();
        unbinder.unbind();
    }

    private void clearListeners() {
        for(FirebaseResourceManager frm : gameVoteListeners) {
            frm.removeListener();
        }
        gameVoteListeners.clear();
    }
}
