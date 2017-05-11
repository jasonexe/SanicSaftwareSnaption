package com.snaptiongame.snaption.ui.wall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseGameResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.GameResourceManager;
import com.snaptiongame.snaption.servercalls.GameType;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.ScrollFabHider;
import com.snaptiongame.snaption.ui.games.GameActivity;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallFragment extends Fragment {
    private static final String GAME_TYPE_EXCEPTION_MSG = "Unset game type. Make sure to call " +
            "newInstance rather than the constructor directly";
    private static final String GAME_TYPE = "game_type";
    private static final int NUM_COLUMNS = 2;
    private static final int SCROLL_DOWN_CONST = 1;
    private static final double PERCENT_BEFORE_LOAD = .1; // Load games if 10 % is left to scroll
    private Unbinder unbinder;
    private WallViewAdapter wallAdapter;
    private boolean isLoading = false;
    private List<FirebaseResourceManager> gameVoteListeners;

    private ResourceListener<Map<String, Integer>> getGameListener(final int gameNum) {
        return new ResourceListener<Map<String, Integer>>() {
            @Override
            public void onData(Map<String, Integer> data) {
                if (isVisible() || !isRemoving()) {
                    wallAdapter.gameChanged(gameNum, data);
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        };
    }

    private GameType gameType;

    private ResourceListener<List<GameMetadata>> listener = new ResourceListener<List<GameMetadata>>() {
        @Override
        public void onData(List<GameMetadata> games) {
            if (isVisible() || !isRemoving()) {
                if (games == null) {
                    Snackbar.make(wallListView, wallListView.getResources().getString(R.string.private_game_error), Snackbar.LENGTH_LONG).show();
                } else {
                    for (GameMetadata curGame : games) {
                        ResourceListener<Map<String, Integer>> gameListener =
                                getGameListener(gameVoteListeners.size());
                        FirebaseResourceManager manager = new FirebaseResourceManager();
                        String upvotesPath = curGame.getIsPublic() ?
                                String.format(Constants.GAME_PUBLIC_METADATA_UPVOTES_PATH,
                                        curGame.getId()) :
                                String.format(Constants.GAME_PRIVATE_METADATA_UPVOTES_PATH,
                                        curGame.getId());
                        manager.retrieveMapWithUpdates(upvotesPath, gameListener);
                        gameVoteListeners.add(manager);
                    }
                }
                wallAdapter.addItems(games);
                isLoading = false;
                if (refreshLayout != null) {
                    refreshLayout.setRefreshing(false);
                }
            }
        }

        @Override
        public Class getDataType() {
            return GameMetadata.class;
        }
    };
    private GameResourceManager resourceManager;

    @BindView(R.id.wall_list)
    protected RecyclerView wallListView;
    @BindView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private SwipeRefreshLayout.OnRefreshListener refreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            clearListeners();
            wallAdapter.clearItems();
            resourceManager = new FirebaseGameResourceManager(10, 10, listener, gameType);
            loadMoreGames();
        }
    };

    public static WallFragment newInstance(GameType gameType) {
        WallFragment fragment = new WallFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(GAME_TYPE, gameType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // This will clear fragments if they get stuck above other ones. Can't figure out how to
        // make them never get stuck, see http://stackoverflow.com/questions/18309815/fragments-displayed-over-each-other
        // But this will at least not force users to reload the app
        if(container != null) {
            container.clearDisappearingChildren();
        }
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        unbinder = ButterKnife.bind(this, view);
        gameVoteListeners = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null && args.getSerializable(GAME_TYPE) != null) {
            gameType = (GameType) args.getSerializable(GAME_TYPE);
            resourceManager = new FirebaseGameResourceManager(10, 10, listener, gameType);
        }
        else {
            throw new RuntimeException(GAME_TYPE_EXCEPTION_MSG);
        }
        setupWallList();
        //set up fab scroll listener
        FloatingActionButton fab = (FloatingActionButton)this.getActivity().findViewById(R.id.fab);
        ScrollFabHider scrollFabHider = new ScrollFabHider(fab, 0);
        wallListView.addOnScrollListener(scrollFabHider);

        wallListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isLoading && wallListView != null) {
                    // offset + extent = range
                    int offset = wallListView.computeVerticalScrollOffset();
                    int range = wallListView.computeVerticalScrollRange();
                    double extentBeforeLoad = wallListView.computeVerticalScrollExtent() * PERCENT_BEFORE_LOAD;
                    int scrolledSoFar = offset + range;
                    if(scrolledSoFar > extentBeforeLoad) {
                        loadMoreGames();
                    }
                }
            }
        });

        refreshLayout.setOnRefreshListener(refreshListener);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        loadMoreGames();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.snaption_wall));
        return view;
    }

    private void setupWallList() {
        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS,
                StaggeredGridLayoutManager.VERTICAL);
        wallListView.setLayoutManager(manager);
        wallListView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));
        wallAdapter = new WallViewAdapter(new ArrayList<GameMetadata>(),
                ProfileActivity.getProfileActivityCreator(getContext()));
        wallAdapter.setOnClickGamePhotoListener(new WallViewAdapter.OnClickGamePhotoListener() {
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
        wallListView.setAdapter(wallAdapter);
    }

    private void loadMoreGames() {
        if (isVisible() || !isRemoving()) {
            isLoading = true;
            if (wallAdapter.getItemCount() == 0 && refreshLayout != null) {
                refreshLayout.setRefreshing(true);
            }
            resourceManager.retrieveGames();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(false);
        clearListeners();
        wallAdapter.clearItems();
        unbinder.unbind();
    }

    private void clearListeners() {
        for(FirebaseResourceManager frm : gameVoteListeners) {
            frm.removeListener();
        }
        gameVoteListeners.clear();
    }
}
