package com.snaptiongame.snaption.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
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
    @BindView(R.id.do_not_show)
    protected CheckBox doNotShowView;

    private Unbinder unbinder;

    public static HelpDialogFragment newInstance(String title, int drawable,
                                                 String sharedPrefValue) {
        HelpDialogFragment helpDialogFragment = new HelpDialogFragment();
        Bundle args = new Bundle();
        args.putString(TITLE_ARG, title);
        args.putString(SHARED_PREF_ARG, sharedPrefValue);
        args.putInt(DRAWABLE_ARG, drawable);
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
        int drawableId = getArguments().getInt(DRAWABLE_ARG);
        helpImage.setImageResource(drawableId);
        helpImage.setVisibility(drawableId == 0 ? View.GONE : View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(customView);
        builder.setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // if don't show again is checked, set the shared preference value to false
                if (doNotShowView.isChecked()) {
                    SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(getArguments().getString(SHARED_PREF_ARG), false);
                    editor.apply();
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
