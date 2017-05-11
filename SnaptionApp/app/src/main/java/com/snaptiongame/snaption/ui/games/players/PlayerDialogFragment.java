package com.snaptiongame.snaption.ui.games.players;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.snaptiongame.snaption.R;
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
    private static final String PLAYERS_ARG = "players_arg";
    private static final String PICKER_ID = "picker_id_arg";

    @BindView(R.id.user_list)
    protected RecyclerView playerList;

    private Unbinder unbinder;

    public static PlayerDialogFragment getInstance(String title, List<String> playerIds,
                                                   String pickerId) {
        PlayerDialogFragment dialogFragment = new PlayerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TITLE_ARG, title);
        bundle.putString(PICKER_ID, pickerId);
        if (playerIds instanceof ArrayList) {
            bundle.putStringArrayList(PLAYERS_ARG, (ArrayList<String>) playerIds);
        }
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

        if (getArguments() != null && getArguments().getStringArrayList(PLAYERS_ARG) != null) {
            List<String> userIds = getArguments().getStringArrayList(PLAYERS_ARG);
            String pickerId = getArguments().getString(PICKER_ID);
            // initialize the players list
            UserListViewModel userListViewModel = new PlayerListViewModel(userIds, pickerId);
            playerList.setAdapter(userListViewModel.getAdapter(getContext()));
        }

        // build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
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
