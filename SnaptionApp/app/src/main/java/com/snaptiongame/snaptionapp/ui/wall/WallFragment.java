package com.snaptiongame.snaptionapp.ui.wall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseGameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.GameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.GameType;
import com.snaptiongame.snaptionapp.servercalls.LoginManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.ScrollFabHider;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallFragment extends Fragment {
    public static final String PRIVATE_GAME_FRAG = "private_game_fragment";
    public static final String PUBLIC_GAME_FRAG = "public_game_fragment";
    public static final String MIXED_GAME_FRAG = "mixed_game_fragment";
    private static final String GAME_TYPE_EXCEPTION_MSG = "Unset game type. Make sure to call " +
            "newInstance rather than the constructor directly";
    private static final String GAME_TYPE = "game_type";
    private static final int NUM_COLUMNS = 2;
    private static final int SCROLL_DOWN_CONST = 1;
    private Unbinder unbinder;
    private WallViewAdapter wallAdapter;
    private boolean isLoading = false;
    private LoginDialog loginDialog;
    private LoginManager loginManager;

    private GameType gameType;
    private ResourceListener<List<Game>> listener = new ResourceListener<List<Game>>() {
        @Override
        public void onData(List<Game> games) {
            if(games == null) {
                Snackbar.make(wallListView, wallListView.getResources().getString(R.string.private_game_error), Snackbar.LENGTH_LONG).show();
            }
            wallAdapter.addItems(games);
            isLoading = false;
            if (refreshLayout != null) {
                refreshLayout.setRefreshing(false);
            }
        }

        @Override
        public Class getDataType() {
            return Game.class;
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
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null && args.getSerializable(GAME_TYPE) != null) {
            gameType = (GameType) args.getSerializable(GAME_TYPE);
            resourceManager = new FirebaseGameResourceManager(10, 10, listener, gameType);
        }
        else {
            throw new RuntimeException(GAME_TYPE_EXCEPTION_MSG);
        }

        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS,
                StaggeredGridLayoutManager.VERTICAL);
        wallListView.setLayoutManager(manager);
        wallListView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));

        wallAdapter = new WallViewAdapter(new ArrayList<Game>(), (MainSnaptionActivity)getActivity());
        wallListView.setAdapter(wallAdapter);
        //set up fab scroll listener
        FloatingActionButton fab = (FloatingActionButton)this.getActivity().findViewById(R.id.fab);
        ScrollFabHider scrollFabHider = new ScrollFabHider(fab, 0);
        wallListView.addOnScrollListener(scrollFabHider);

        wallListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isLoading && !wallListView.canScrollVertically(SCROLL_DOWN_CONST)) {
                    loadMoreGames();
                }
            }
        });

        refreshLayout.setOnRefreshListener(refreshListener);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        loadMoreGames();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.snaption_wall));
        return view;
    }

    private void loadMoreGames() {
        isLoading = true;
        if (wallAdapter.getItemCount() == 0) {
            refreshLayout.setRefreshing(true);
        }
        resourceManager.retrieveGames();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (FirebaseResourceManager frm : wallAdapter.resourceManagerMap.values()) {
            frm.removeListener();
        }
        unbinder.unbind();
    }
}
