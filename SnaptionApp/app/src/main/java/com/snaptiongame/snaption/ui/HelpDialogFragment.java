package com.snaptiongame.snaption.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaptiongame.snaption.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by brittanyberlanga on 5/25/17.
 */

public class HelpDialogFragment extends DialogFragment {

    private static final String TITLE_ARG = "title_arg";
    private static final String DRAWABLE_ARG = "drawable_arg";
    private static final String SHARED_PREF_ARG = "shared_preferences_arg";

    @BindView(R.id.title)
    protected TextView title;
    @BindView(R.id.help_image)
    protected ImageView helpImage;

    private Unbinder unbinder;

    public static HelpDialogFragment newInstance(String title, int drawable,
                                                 String sharedPrefValue) {
        HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_ARG, title);
        args.putInt(DRAWABLE_ARG, drawable);
        args.putString(SHARED_PREF_ARG, sharedPrefValue);
        helpDialogFragment.setArguments(args);
        return helpDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // initialize the custom view
        View customView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_help, null);
        unbinder = ButterKnife.bind(this, customView);

        title.setText(getArguments().getString(TITLE_ARG));
        helpImage.setImageResource(getArguments().getInt(DRAWABLE_ARG));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView);
        builder.setPositiveButton("ok", null);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
