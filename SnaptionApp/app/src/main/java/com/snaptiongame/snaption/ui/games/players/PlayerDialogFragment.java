package com.snaptiongame.snaption.ui.games.players;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.user.UserListViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 5/9/17.
 */

public class PlayerDialogFragment extends DialogFragment {
    private static final String TITLE_ARG = "title_arg";
    private static final String GAME_ARG = "game_arg";

    @BindView(R.id.user_list)
    protected RecyclerView playerList;

    private Unbinder unbinder;

    public static PlayerDialogFragment getInstance(String title, Game game) {
        PlayerDialogFragment dialogFragment = new PlayerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_ARG, title);
        bundle.putSerializable(GAME_ARG, game);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize the custom view
        View customView = LayoutInflater.from(getContext())
                .inflate(R.layout.fragment_user_dialog, null);
        unbinder = ButterKnife.bind(this, customView);

        if (getArguments() != null && getArguments().getSerializable(GAME_ARG) != null) {
            Game game = (Game) getArguments().getSerializable(GAME_ARG);
            List<String> userIds = new ArrayList<>(game.getPlayers().keySet());
            // remove the picker from the list if in the players list
            userIds.remove(game.getPickerId());
            // add the picker to the beginning of the list
            userIds.add(0, game.getPickerId());

            // initialize the players list
            UserListViewModel userListViewModel = new PlayerListViewModel(userIds,
                    game, new Uploader.UploadListener() {
                @Override
                public void onComplete() {
                    // removing player from game was successful
                    dismiss();
                    if (getActivity() != null && getActivity().getCurrentFocus() != null) {
                        Snackbar.make(getActivity().getCurrentFocus(), getContext()
                                .getText(R.string.leave_game_success), Snackbar.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    // removing player from game failed
                    dismiss();
                    if (getActivity() != null && getActivity().getCurrentFocus() != null) {
                        Snackbar.make(getActivity().getCurrentFocus(), getContext()
                                .getText(R.string.leave_game_error), Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            playerList.setAdapter(userListViewModel.getAdapter(getContext()));
        }

        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(customView);
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(TITLE_ARG))) {
            builder.setTitle(getArguments().getString(TITLE_ARG));
        }
        builder.setPositiveButton(R.string.close, null);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
