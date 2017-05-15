package com.snaptiongame.snaption.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.snaptiongame.snaption.MainSnaptionActivity;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.FirebaseReporter;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.ui.games.PhotoZoomActivity;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.utilities.BitmapConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_METADATA_PATH;

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
    @BindView(R.id.profile_tab_layout)
    protected TabLayout tabLayout;
    @BindView(R.id.profile_pager)
    protected ViewPager viewPager;

    private Unbinder unbinder;
    private ProfileFragmentPagerAdapter pagerAdapter;
    private Drawable oldProfilePic;
    private Uri newPhotoUri;
    private User thisUser;
    private FloatingActionButton fab;
    private boolean isUser;

    private UserInfoEditListener userInfoEditListener;
    public interface UserInfoEditListener {
        /**
         * Callback for when the username is edited. If errorMessage is null, the username was
         * successfully edited. Otherwise, an error occurred while editing the username and
         * errorMessage contains the details of the error
         */
        void onEditUsername(String errorMessage);
        /**
         * Callback for when the profile photo is edited. If errorMessage is null, the profile photo
         * was successfully edited. Otherwise, an error occurred while editing the profile photo and
         * errorMessage contains the details of the error
         */
        void onEditPhoto(String errorMessage);
    }

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

        pagerAdapter = new ProfileFragmentPagerAdapter(getChildFragmentManager(), getContext());

        //if the user is logged in
        if (userId != null) {
            //retrieve information from User table
            FirebaseUserResourceManager.getUserById(userId, new ResourceListener<User>() {
                @Override
                public void onData(User user) {
                    setupUserData(user);
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
        return view;
    }

    private void setupUserData(User user) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(user.getDisplayName());
        // If the view is hidden (IE user switched to another fragment) Then we don't have to update anything
        if(this.isHidden()) {
            return;
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
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

        String userId = FirebaseUserResourceManager.getUserId();
        //check if we need to show the addFriend button
        //if we came from the Profile Activity and these two users are not already friends
        if (getActivity() instanceof ProfileActivity
                && user != null && userId != null
                && (user.getFriends() == null || !user.getFriends().containsKey(userId))
                && !isUser) {
            ((ProfileActivity) getActivity()).setAddFriendVisible(true);
        }

        //get the games based on list of games in user
        thisUser = user;
        //set up tabbed view for captions and games
        pagerAdapter.setUser(user);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        pagerAdapter.notifyDataSetChanged();
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

    public User getUser() {
        return thisUser;
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
        final String newText = profileEditName.getText().toString().trim();
        // Firebase stuff here
        if (!newText.isEmpty() && !newText.equals(thisUser.getDisplayName())) {
            userName.setText(newText);

            FirebaseUploader.updateDisplayName(newText, thisUser.getId(),
                    new Uploader.UploadListener() {
                @Override
                public void onComplete() {
                    thisUser.setDisplayName(newText);
                    if (userInfoEditListener != null) {
                        userInfoEditListener.onEditUsername(null);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    userName.setText(thisUser.getDisplayName());
                    ((AppCompatActivity) getActivity()).getSupportActionBar()
                            .setTitle(thisUser.getDisplayName());
                    if (userInfoEditListener != null) {
                        userInfoEditListener.onEditUsername(errorMessage);
                    }
                }
            });
        }
    }

    // Hides the camera overlay and disables clicking on the profile picture
    private void hideEditProfilePic() {
        editPhotoOverlay.setVisibility(View.GONE);
        profile.setClickable(false);
    }

    // Saves the profile picture to firebase and keeps the updated one on the profile
    private void saveProfilePic() {
        if(newPhotoUri != null) {
            byte[] newPhoto = BitmapConverter.getImageFromUri(newPhotoUri, getActivity());
            clearGlideCache();
            FirebaseUploader.uploadUserPhoto(thisUser.getImagePath(), newPhoto,
                    new Uploader.UploadListener() {
                @Override
                public void onComplete() {
                    if (userInfoEditListener != null) {
                        userInfoEditListener.onEditPhoto(null);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    FirebaseResourceManager.loadImageIntoView(thisUser.getImagePath(), profile);
                    if (userInfoEditListener != null) {
                        userInfoEditListener.onEditPhoto(errorMessage);
                    }
                }
            });
        }
    }

    private void clearGlideCache() {
        Glide.get(getApplicationContext()).clearMemory();
        AsyncTask clearGlideCache = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Glide.get(getApplicationContext()).clearDiskCache();
                return null;
            }
        };
        clearGlideCache.execute();
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
                newPhotoUri = data.getData();
                Glide.with(ProfileFragment.this).load(newPhotoUri).into(profile);
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

    public void setUserInfoEditListener(UserInfoEditListener listener) {
        this.userInfoEditListener = listener;
    }
}
