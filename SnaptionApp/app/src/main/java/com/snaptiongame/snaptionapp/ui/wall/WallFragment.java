package com.snaptiongame.snaptionapp.ui.wall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.snaptiongame.snaptionapp.servercalls.GameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.GameType;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.ScrollFabHider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 1/12/17.
 */

public class WallFragment extends Fragment {
    private static final int NUM_COLUMNS = 2;
    private Unbinder unbinder;
    private WallViewAdapter wallAdapter;
    private boolean isLoading = false;
    private ResourceListener<List<Game>> listener = new ResourceListener<List<Game>>() {
        @Override
        public void onData(List<Game> games) {
            wallAdapter.addItems(games);
            isLoading = false;
        }

        @Override
        public Class getDataType() {
            return Game.class;
        }
    };
    private GameResourceManager resourceManager = new FirebaseGameResourceManager(10, 10, listener, GameType.MIXED_GAMES);

    @BindView(R.id.wall_list)
    protected RecyclerView wallListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        unbinder = ButterKnife.bind(this, view);
        final StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        wallListView.setLayoutManager(manager);
        wallListView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));

        wallAdapter = new WallViewAdapter(new ArrayList<Game>(), (MainSnaptionActivity)getActivity());
        wallListView.setAdapter(wallAdapter);
        //set up fab scroll listener
        FloatingActionButton fab = (FloatingActionButton)this.getActivity().findViewById(R.id.fab);
        ScrollFabHider scrollFabHider = new ScrollFabHider(fab, ScrollFabHider.BIG_HIDE_THRESHOLD);
        wallListView.addOnScrollListener(scrollFabHider);

        wallListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalChildren = manager.getItemCount();
                int totalChildrenVisible = manager.getChildCount();
                int[] firstVisibleChildren = null;
                firstVisibleChildren = manager.findFirstVisibleItemPositions(firstVisibleChildren);
                if (!isLoading && firstVisibleChildren != null && firstVisibleChildren.length > 0 &&
                        firstVisibleChildren[0] + totalChildrenVisible > totalChildren) {
                    loadMoreGames();
                }
            }
        });

        loadMoreGames();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.snaption_wall));
        return view;
    }

    private void loadMoreGames() {
        isLoading = true;
        resourceManager.retrieveGames();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
