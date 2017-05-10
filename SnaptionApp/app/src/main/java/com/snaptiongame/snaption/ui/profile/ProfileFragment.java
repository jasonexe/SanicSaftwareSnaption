package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaption.MainSnaptionActivity;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.games.PhotoZoomActivity;
import com.snaptiongame.snaption.utilities.BitmapConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaptiongame.snaption.Constants.*;

/**
 * Created by austinrobarts on 1/23/17.
 */
public class ProfileFragment extends Fragment {

    public static final String USER_ID_ARG = "userId";

    private static final int IMAGE_PICK_CODE = 8;
    private boolean isEditing;

    @BindView(R.id.profile_picture)
    public ImageView profile;
    @BindView(R.id.profile_name)
    public TextView userName;
    @BindView(R.id.games_created)
    public TextView gamesCreated;
    @BindView(R.id.captions_created)
    public TextView captionsCreated;
    @BindView(R.id.profile_games_list)
    protected RecyclerView gameListView;
    @BindView(R.id.profile_captions_list)
    protected RecyclerView captionsListView;
    @BindView(R.id.profile_name_editable)
    protected EditText profileEditName;
    @BindView(R.id.stop_name_change)
    protected ImageView nameChangeCancel;
    @BindView(R.id.edit_photo_overlay)
    protected ImageView editPhotoOverlay;
    @BindView(R.id.friends_made)
    protected TextView friendsMade;
    @BindView(R.id.total_caption_upvotes)
    protected TextView totalCapUpvotes;

    private Unbinder unbinder;
    private ProfileGamesAdapter gameAdapter;
    private ProfileCaptionsAdapter captionsAdapter;
    private Drawable oldProfilePic;
    private byte[] newPhoto;
    private User thisUser;
    private FloatingActionButton fab;
    private boolean isUser;
    private ResourceListener gameListener = new ResourceListener<GameMetadata>() {
        @Override
        public void onData(GameMetadata data) {
            // filter out private games if needed
            //TODO get private games for user
            gameAdapter.addGame(data);
        }

        @Override
        public Class getDataType() {
            return GameMetadata.class;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        String userId = getArguments().getString(ProfileFragment.USER_ID_ARG);
        String currentUserId = FirebaseUserResourceManager.getUserId();
        isUser = currentUserId != null && currentUserId.equals(userId);
        isEditing = false;
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        // Set clickability to false - for some reason doesn't work in XML (maybe b/c butterknife?)
        profile.setClickable(false);

        //set up all recycler view connections
        LinearLayoutManager gameViewManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        gameListView.setLayoutManager(gameViewManager);
        gameAdapter = new ProfileGamesAdapter(new ArrayList<GameMetadata>());
        gameListView.setAdapter(gameAdapter);

        //if the user is logged in
        if (userId != null) {
            //retrieve information from User table
            FirebaseUserResourceManager.getUserById(userId, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    setupUserData(user, view);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
        return view;
    }

    private void setupUserData(User user, View view) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user.getDisplayName());
        if(this.isHidden()) {
            return;
        }
        userName.setText(user.getDisplayName());
        FirebaseResourceManager.loadImageIntoView(user.getImagePath(), profile);
        gamesCreated.setText(String.valueOf(user.getTotalCreatedGamesCount()));
        captionsCreated.setText(String.valueOf(user.getTotalCaptionCount()));
        friendsMade.setText(String.valueOf(user.getFriendCount()));

        int numCapUpvotes = 0;

        if(user.getPublicCaptions() != null) {
            for(Caption caption : user.getPublicCaptions().values()) {
                numCapUpvotes += caption.retrieveNumUpvotes();
            }
        }
        totalCapUpvotes.setText(String.valueOf(numCapUpvotes));

        //get the games based on list of games in user
        thisUser = user;
        getUserGames(user);
        getUserCaptions(user, view);
    }

    private void getUserCaptions(User user, View view) {
        LinearLayoutManager captionViewManager = new LinearLayoutManager(view.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        captionsListView.setLayoutManager(captionViewManager);
        List<Caption> captions = new ArrayList<>();
        if (isUser) {
            //get public and private captions
            captions = user.getAllCaptions();
        }
        else {
            //get private games only
            captions = user.getAllPublicCaptions();
        }
        captionsAdapter = new ProfileCaptionsAdapter(captions);
        captionsListView.setAdapter(captionsAdapter);
    }

    /*private boolean canDisplayGame(GameMetadata game, boolean isUser) {
        return game != null && (isUser ||
                game.getIsPublic() ||
                FirebaseUserResourceManager.getUserId() != null && game.getPlayers() != null &&
                        game.getPlayers().containsKey(FirebaseUserResourceManager.getUserId()));
    }*/

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

    TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId,
                                      KeyEvent event) {
            System.out.println(actionId);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                fabClicked(fab, true);
            }
            return true;
        }
    };

    @OnClick(R.id.friends_container)
    public void goToFriendsList() {
        // If the fragment is part of the MainSnaptionActivity, then switch
        // Otherwise, don't do anything.
        if(getActivity() instanceof MainSnaptionActivity) {
            MainSnaptionActivity activity = (MainSnaptionActivity) getActivity();
            activity.switchFragments(R.id.friends_item);
        }
    }

    @OnClick(R.id.stop_name_change)
    public void cancelChanges() {
        fabClicked(fab, false);
    }

    @OnClick(R.id.profile_picture_container)
    public void enlargePhoto() {
        if(thisUser == null) {
            // Hasn't loaded user yet, short circuit
            return;
        }
        Intent photoZoomIntent = new Intent(getActivity(), PhotoZoomActivity.class);
        photoZoomIntent.putExtra(PhotoZoomActivity.PHOTO_PATH, thisUser.getImagePath());
        photoZoomIntent.putExtra(PhotoZoomActivity.TRANSITION_NAME, thisUser.getId());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                profile, thisUser.getId());
        startActivity(photoZoomIntent, options.toBundle());
    }

    public void fabClicked(FloatingActionButton fab, boolean save) {
        // If user was editing their stuff, save it
        this.fab = fab;
        if(isEditing) {
            if(save) {
                saveInput();
            } else {
                cancelSave();
            }
            fab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        } else {
            editDisplayName();
            editProfilePic();
            fab.setImageResource(R.drawable.ic_save_white_24dp);
        }
        isEditing = !isEditing;
    }

    private void cancelSave() {
        hideEditName();
        resetProfile();
        hideEditProfilePic();
    }

    private void resetProfile() {
        profile.setImageDrawable(oldProfilePic);
    }

    // When the fab is clicked while editing, this will be called
    private void saveInput() {
        hideEditName();
        saveEditName();
        hideEditProfilePic();
        saveProfilePic();
    }

    // Disables the ability to edit the name and also hides the keyboard
    private void hideEditName() {
        profileEditName.clearComposingText();
        profileEditName.setVisibility(View.GONE);
        nameChangeCancel.setVisibility(View.GONE);
        userName.setVisibility(View.VISIBLE);
        hideKeyboard();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(profileEditName.getWindowToken(), 0);
    }

    // Saves the name to firebase, also updates the name in the profile
    private void saveEditName() {
        // Firebase stuff here
        String newText = profileEditName.getText().toString().trim();
        if (!newText.isEmpty() && !newText.equals(thisUser.getDisplayName())) {
            FirebaseUploader.updateDisplayName(newText, thisUser.getId());
            thisUser.setDisplayName(newText);
            userName.setText(newText);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(newText);
        }
    }

    // Hides the camera overlay and disables clicking on the profile picture
    private void hideEditProfilePic() {
        editPhotoOverlay.setVisibility(View.GONE);
        profile.setClickable(false);
    }

    // Saves the profile picture to firebase and keeps the updated one on the profile
    private void saveProfilePic() {
        if(newPhoto != null) {
            clearGlideCache();
            FirebaseUploader.uploadUserPhoto(thisUser.getImagePath(), newPhoto);
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setMessage(getResources().getString(R.string.picture_change)).create();
            dialog.show();
        }
    }

    private void clearGlideCache() {
        AsyncTask clearGlideCache = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Glide.get(getApplicationContext()).clearDiskCache();
                return null;
            }
        };
        clearGlideCache.execute();
        Glide.get(getApplicationContext()).clearMemory();
    }

    private void editDisplayName() {
        userName.setVisibility(View.INVISIBLE);
        profileEditName.setOnEditorActionListener(enterListener);
        profileEditName.setText(userName.getText());
        profileEditName.setVisibility(View.VISIBLE);
        nameChangeCancel.setVisibility(View.VISIBLE);
        profileEditName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(profileEditName, InputMethodManager.SHOW_IMPLICIT);
    }

    private void editProfilePic() {
        editPhotoOverlay.setVisibility(View.VISIBLE);
        profile.setClickable(true);
        oldProfilePic = profile.getDrawable();
    }

    @OnClick(R.id.profile_picture)
    public void onClickAddPhoto() {
        //Gets the content from the imageview
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Choose Image from..."), IMAGE_PICK_CODE);
    }

    // Sets the image in the imageview
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_PICK_CODE) {
            try {
                Uri imageUri = data.getData();
                Glide.with(ProfileFragment.this).load(imageUri).into(profile);
                newPhoto = BitmapConverter.getImageFromUri(imageUri, getActivity());
            } catch (Exception e) {
                FirebaseReporter.reportException(e, "Couldn't read user's photo data");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
