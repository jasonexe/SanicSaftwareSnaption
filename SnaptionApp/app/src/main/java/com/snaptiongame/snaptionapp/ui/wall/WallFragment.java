package com.snaptiongame.snaptionapp.ui.wall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseGameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.GameResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;

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
    private ResourceListener<List<Game>> listener = new ResourceListener<List<Game>>() {
        @Override
        public void onData(List<Game> games) {
            wallAdapter.addItems(games);
        }

        @Override
        public Class getDataType() {
            return Game.class;
        }
    };
    private GameResourceManager resourceManager = new FirebaseGameResourceManager(8, listener);

    public void retrieveMoreGames() {
        resourceManager.retrieveGamesByCreationDate();
    }

    @BindView(R.id.wall_list)
    protected RecyclerView wallListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        unbinder = ButterKnife.bind(this, view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(NUM_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        wallListView.setLayoutManager(manager);
        wallListView.addItemDecoration(new WallGridItemDecorator(getResources().getDimensionPixelSize(R.dimen.wall_grid_item_spacing)));

        wallAdapter = new WallViewAdapter(new ArrayList<Game>());
        wallListView.setAdapter(wallAdapter);

        retrieveMoreGames();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
