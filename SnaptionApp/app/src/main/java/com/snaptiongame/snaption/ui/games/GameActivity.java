package com.snaptiongame.snaption.ui.games;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Card;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.GameData;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.servercalls.ChildResourceListener;
import com.snaptiongame.snaption.servercalls.FirebaseDeepLinkCreator;
import com.snaptiongame.snaption.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaption.servercalls.FirebaseUploader;
import com.snaptiongame.snaption.servercalls.FirebaseUserResourceManager;
import com.snaptiongame.snaption.servercalls.LoginManager;
import com.snaptiongame.snaption.servercalls.ResourceListener;
import com.snaptiongame.snaption.servercalls.Uploader;
import com.snaptiongame.snaption.ui.HomeAppCompatActivity;
import com.snaptiongame.snaption.ui.ScrollFabHider;
import com.snaptiongame.snaption.ui.games.add_friend_to_game.AddToGameDialog;
import com.snaptiongame.snaption.ui.login.LoginDialog;
import com.snaptiongame.snaption.ui.profile.ProfileActivity;
import com.snaptiongame.snaption.utilities.BitmapConverter;
import com.snaptiongame.snaption.utilities.ColorUtilities;
import com.snaptiongame.snaption.utilities.ViewUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaption.Constants.GAME_DATA_PATH;
import static com.snaptiongame.snaption.Constants.GAME_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_DATA_CAPTIONS_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_DATA_PLAYERS_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_DATA_CAPTIONS_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_DATA_PLAYERS_PATH;
import static com.snaptiongame.snaption.Constants.GOOGLE_LOGIN_RC;
import static com.snaptiongame.snaption.Constants.MILLIS_PER_SECOND;
import static com.snaptiongame.snaption.Constants.PRIVATE;
import static com.snaptiongame.snaption.Constants.PUBLIC;
import static com.snaptiongame.snaption.ui.games.CardLogic.addCaption;
import static com.snaptiongame.snaption.ui.games.CardLogic.getRandomCardsFromList;

/**
 * This class is the core of the game screen, is in charge of basically all the UI-related, and some
 * logic-related Game code. This Activity is started when a user clicks on a photo on the wall
 * TODO needs to verify that a user is logged in before adding captions.
 *
 * @Author Jason Krein, Cameron Geehr
 */
public class GameActivity extends HomeAppCompatActivity {
    public static final String USE_GAME_ID = "useGameId";
    public static final String USE_GAME_ACCESS = "useGameAccess";
    public static final String REFRESH_STRING = "refresh";
    public static final String BLANK_CARD = "__blank__";
    private final static String EMPTY_SIZE = "0";
    private static final int MAX_PREFILLED_LENGTH = 30;
    private static final int MAX_CUSTOM_LENGTH = 50;
    private static final int ROTATION_TIME = 600;
    private static final float FAB_ROTATION = 135f;
    private static final long BITMAP_ANIM_DURATION = 1000L;
    private static final float MAX_IMAGE_HEIGHT_PERCENT = 0.5f;
    private List<Card> allCards = null;
    private List<Card> handCards = null;
    private Card curUserCard = null;
    private CardOptionsAdapter cardListAdapter;

    private Game game;
    private String photoPath;
    private GameCaptionViewAdapter captionAdapter;
    private FirebaseResourceManager commentManager;
    private FirebaseResourceManager joinedGameManager;

    private LoginManager loginManager;
    private LoginDialog loginDialog;

    private MinimizeViewBehavior minimizeImageBehavior;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.image_view)
    protected ImageView imageView;

    @BindView(R.id.picker_photo)
    protected ImageView pickerPhoto;

    @BindView(R.id.picker_name)
    protected TextView pickerName;

    @BindView(R.id.flag_icon)
    protected ImageView flag;

    @BindView(R.id.number_captions)
    protected TextView numberCaptions;

    @BindView(R.id.text_date)
    protected TextView endDate;

    @BindView(R.id.recycler_caption_list)
    protected RecyclerView captionListView;

    // Since the view will be formatted as "Card text" "Input Field" "More card text"
    // Need to get the first half of card text, and second half (hence the two halves)
    @BindView(R.id.first_half_text)
    public TextView firstHalfCardText;

    @BindView(R.id.second_half_text)
    public TextView secondHalfCardText;

    @BindView(R.id.edit_caption_text)
    public EditText editCaptionText;

    @BindView(R.id.card_input)
    public View cardInputView;

    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @BindView(R.id.possible_caption_cards_list)
    public RecyclerView captionCardsList;

    private ScrollFabHider scrollFabHider;

    @BindView(R.id.invite_friends)
    public Button inviteFriendsButton;

    @BindView(R.id.join_game_button)
    public Button joinGameButton;

    @BindView(R.id.coord_layout)
    protected CoordinatorLayout coordinatorLayout;

    @BindView(R.id.game_content)
    public LinearLayout gameContentLayout;

    @BindView(R.id.picker_container)
    public RelativeLayout pickerContainer;

    @BindView(R.id.date_container)
    public RelativeLayout dateContainer;

    @BindView(R.id.progress_bar)
    public View progressBar;

    @BindView(R.id.leave_button)
    public Button leaveButton;

    private ChildResourceListener<Caption> captionListener = new ChildResourceListener<Caption>() {
        @Override
        public void onNewData(Caption data) {
            if (data != null) {
                captionAdapter.addCaption(data);
                numberCaptions.setText(String.format(Locale.getDefault(),
                        "%d", captionAdapter.getItemCount()));
            }
        }

        @Override
        public void onDataChanged(Caption data) {
            captionAdapter.captionChanged(data);
        }

        @Override
        public Class getDataType() {
            return Caption.class;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        setupToolbar(toolbar);

        // add scrolling behavior to progress bar
        minimizeImageBehavior = new MinimizeViewBehavior(gameContentLayout);
        ((CoordinatorLayout.LayoutParams) progressBar.getLayoutParams())
                .setBehavior(minimizeImageBehavior);

        Intent startedIntent = getIntent();
        // If started from the wall, we'll have been sent the Game object, so can use that for stuff
        if (startedIntent.hasExtra(Constants.GAME)) {
            GameMetadata metadata = (GameMetadata) getIntent().getSerializableExtra(Constants.GAME); //Obtaining data
            // set the shared transition view name
            ViewCompat.setTransitionName(imageView, metadata.getId());
            // postpone transition til image is loaded
            setupGameMetadataElements(metadata);
            retrieveGameData(metadata.getIsPublic() ? PUBLIC : PRIVATE, metadata.getId(), metadata);
        } else if (startedIntent.hasExtra(USE_GAME_ID) && startedIntent.hasExtra(USE_GAME_ACCESS)) {
            // If we were started via deep link, we'll only have the game ID. Have to pull
            // from firebase
            String gameId = startedIntent.getStringExtra(USE_GAME_ID);
            String access = startedIntent.getStringExtra(USE_GAME_ACCESS);
            //Gets the metadata and data for the game and calls setupGameElements
            retrieveGame(access, gameId);
        }
    }

    private void retrieveGame(String access, String gameId) {
        retrieveGameMetaData(access, gameId);
    }

    private void retrieveGameMetaData(final String access, final String gameId) {
        FirebaseResourceManager.retrieveSingleNoUpdates(
            String.format(GAME_METADATA_PATH, access, gameId),
            new ResourceListener<GameMetadata>() {
                @Override
                public void onData(GameMetadata metadata) {
                    if (metadata != null) {
                        setupGameMetadataElements(metadata);
                        retrieveGameData(access, gameId, metadata);
                    }
                    else {
                        System.err.println("Game activity was passed an incorrect gameId");
                    }
                }

                @Override
                public Class getDataType() {
                    return GameMetadata.class;
                }
            }
        );
    }

    private void retrieveGameData(final String access, final String gameId,
                                  final GameMetadata metaData) {
        FirebaseResourceManager.retrieveSingleNoUpdates(
            String.format(GAME_DATA_PATH, access, gameId), new ResourceListener<GameData>() {
                @Override
                public void onData(GameData data) {
                    if (data != null) {
                        Game game = new Game(data, metaData);
                        setupGameElements(game);
                    } else {
                        GameData emptyData = new GameData();
                        Game game = new Game(emptyData, metaData);
                        setupGameElements(game);
                    }
                }

                @Override
                public Class getDataType() {
                    return GameData.class;
                }
            }
        );
    }

    private void setupGameElements(Game game) {
        this.game = game;
        setupButtonDisplay(game);
        setupCaptionList(game);
        startCommentManager(game.getMetaData());
    }

    private void loadPhoto(GameMetadata metadata) {
        // set the progress bar and image view height using the image aspect ratio
        Resources res = getResources();
        final int imageHeight = ViewUtilities.calculateViewHeight(metadata.getImageAspectRatio(),
                res.getDisplayMetrics().widthPixels, res.getDisplayMetrics().heightPixels * MAX_IMAGE_HEIGHT_PERCENT);
        progressBar.getLayoutParams().height = imageHeight;
        minimizeImageBehavior.updateViewMaxHeight(imageHeight);
        imageView.getLayoutParams().height = imageHeight;
        photoPath = metadata.getImagePath();
        FirebaseResourceManager.loadImageIntoView(photoPath, imageView,
                new ResourceListener<Bitmap>() {
                    @Override
                    public void onData(final Bitmap bitmap) {
                        if (bitmap != null) {
                            // remove the progress bar
                            ((CoordinatorLayout.LayoutParams) progressBar.getLayoutParams())
                                    .setBehavior(null);
                            progressBar.setVisibility(View.GONE);
                            // add a new behavior to the image view
                            minimizeImageBehavior = new MinimizeViewBehavior(gameContentLayout,
                                    imageHeight);
                            ((CoordinatorLayout.LayoutParams) imageView.getLayoutParams())
                                    .setBehavior(minimizeImageBehavior);
                            // start transition now that image is loaded
                            supportStartPostponedEnterTransition();
                            // animate the image color swatch
                            animateBitmapColorSwatch(bitmap);
                        }
                    }

                    @Override
                    public Class getDataType() {
                        return Boolean.class;
                    }
                });

    }

    private void setupGameMetadataElements(GameMetadata metadata) {
        scrollFabHider = new ScrollFabHider(fab, ScrollFabHider.BIG_HIDE_THRESHOLD);
        loadPhoto(metadata);
        initLoginManager();
        setupEndDate(metadata);
        setupPickerName(metadata);
        setupCaptionCardView(metadata);
//        startCommentManager(metadata);
    }

    private void animateBitmapColorSwatch(final Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorUtilities.generateBitmapColor(GameActivity.this, bitmap,
                    new ColorUtilities.ColorListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onColorGenerated(int color) {
                            // change the back arrow to dark grey if the image is light
                            ColorUtilities.generateHomeArrowColor(GameActivity.this,
                                    bitmap, new ColorUtilities.ColorListener() {
                                        @Override
                                        public void onColorGenerated(int arrowColor) {
                                            setupHomeArrow(arrowColor);
                                        }
                                    });
                            // animate the status bar color to the generated color
                            ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                    getWindow().getStatusBarColor(), color);
                            statusBarColorAnim.addUpdateListener(new ValueAnimator
                                    .AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int transitionColor = (int) animation.getAnimatedValue();
                                    getWindow().setStatusBarColor(transitionColor);
                                }
                            });
                            statusBarColorAnim.setDuration(BITMAP_ANIM_DURATION);
                            statusBarColorAnim.setInterpolator(AnimationUtils.loadInterpolator(GameActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            // animate the image background color to the generated color
                            ValueAnimator imageColorAnim = ValueAnimator.ofArgb(
                                    android.R.color.white, color);
                            imageColorAnim.addUpdateListener(new ValueAnimator
                                    .AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int transitionColor = (int) animation.getAnimatedValue();
                                    imageView.setBackgroundColor(transitionColor);
                                }
                            });
                            imageColorAnim.setDuration(BITMAP_ANIM_DURATION);
                            imageColorAnim.setInterpolator(AnimationUtils.loadInterpolator(GameActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            statusBarColorAnim.start();
                            imageColorAnim.start();
                        }
                    });
        }
    }

    private void setupHomeArrow(int arrowColor) {
        if (getSupportActionBar() != null) {
            final Drawable upArrow = ContextCompat
                    .getDrawable(GameActivity.this, R.drawable.back_arrow);
            upArrow.setColorFilter(arrowColor, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private void setupButtonDisplay(Game game) {
        // When this is initially called, setup the button with current data
        final String pickerId = game.getPickerId();
        if (game.getPlayers() == null) {
            determineButtonDisplay(pickerId, null);
        } else {
            determineButtonDisplay(pickerId, game.getPlayers().keySet());
        }
        joinedGameManager = new FirebaseResourceManager();
        // setup a listener for when player joins the game
        String gamePlayersPath = game.getIsPublic() ?
                String.format(GAME_PUBLIC_DATA_PLAYERS_PATH, game.getId()) :
                String.format(GAME_PRIVATE_DATA_PLAYERS_PATH, game.getId());
        joinedGameManager.retrieveMapWithUpdates(gamePlayersPath,
                new ResourceListener<Map<String, Object>>() {
            @Override
            public void onData(Map<String, Object> data) {
                // retrieveMapWithUpdates guaranteed to return a map from string to object
                if (data != null) {
                    determineButtonDisplay(pickerId, data.keySet());
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
        // Make the fab invisible if it's past the end date
        if (!game.isOpen()) {
            fab.hide();
        }
    }

    private void determineButtonDisplay(String pickerId, Set<String> players) {
        String thisUser = FirebaseUserResourceManager.getUserId();
        // If they're not logged in, just show join game
        if (thisUser == null) {
            setJoinGameIsVisible(true);
            return;
        }
        boolean thisPlayerInGame = false;
        // If this user is the picker or they're in the players list, they're in the game
        if (pickerId.equals(thisUser) || (players != null && players.contains(thisUser))) {
            thisPlayerInGame = true;
        }
        // If this is a public game, anyone can send an invite to it if they've joined
        if (game.getIsPublic()) {
            // If they're in the list of players, they can invite
            // If they're not, show them the join button
            setJoinGameIsVisible(!thisPlayerInGame);
        } else {
            // If it's a private game and the picker is logged in, they can invite people
            if (pickerId.equals(thisUser)) {
                setJoinGameIsVisible(false);
            } else {
                // If they're not in the game, set join to visible. Otherwise, not visible
                setJoinGameIsVisible(!thisPlayerInGame);
                // If user logged in isn't the picker, no inviting for them!
                inviteFriendsButton.setVisibility(View.GONE);
            }
        }
        // If they're the picker, they can't leave. Too bad so sad.
        if(pickerId.equals(thisUser)) {
            leaveButton.setVisibility(View.GONE);
        }
    }

    private void setJoinGameIsVisible(boolean isVisible) {
        if (isVisible) {
            inviteFriendsButton.setVisibility(View.GONE);
            joinGameButton.setVisibility(View.VISIBLE);
            leaveButton.setVisibility(View.GONE);
        } else {
            inviteFriendsButton.setVisibility(View.VISIBLE);
            joinGameButton.setVisibility(View.GONE);
            leaveButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupCaptionList(Game game) {
        LinearLayoutManager captionViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        captionListView.setLayoutManager(captionViewManager);
        if (game.getCaptions() != null) {
            numberCaptions.setText(Integer.toString(game.getCaptions().size()));
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<>(game.getCaptions().values()),
                    loginDialog, ProfileActivity.getProfileActivityCreator(this), game.getIsPublic(),
                    game.getEndDate());
        } else {
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<Caption>(),
                    loginDialog, ProfileActivity.getProfileActivityCreator(this), game.getIsPublic(),
                    game.getEndDate());
            numberCaptions.setText(EMPTY_SIZE);
        }
        captionListView.setAdapter(captionAdapter);
        // Make the fab invisible if it's past the end date
        if (game.isOpen()) {
            captionListView.addOnScrollListener(scrollFabHider);
        }
    }

    // Displays the date that the game will end underneath the picture
    private void setupEndDate(GameMetadata game) {
        Calendar calendar = Calendar.getInstance();
        // display when the game ended when closed
        if(!game.isOpen()) {
            // Multiply end date by 1,000 because the dates in firebase are in seconds, not ms
            calendar.setTimeInMillis(game.getEndDate() * MILLIS_PER_SECOND);
            endDate.setText(new SimpleDateFormat(getResources().getString(R.string.end_date), Locale.getDefault())
                    .format(calendar.getTime()));
        }
        else {
            // remainingTime returns as an array to have the first element be the value
            // and to have the second element be an R.string id, to avoid passing in Contexts
            int[] format = game.remainingTime(calendar.getTimeInMillis());
            String toShow = format[0] + " " + getResources().getString(format[1]);
            // indicator to include the "s" for plural
            if (format[0] != 1) {
                toShow += getResources().getString(R.string.plural);
            }
            endDate.setText(toShow + " " + getResources().getString(R.string.remain));
        }
    }



    // Displays the name of the picture underneath the picture, and
    // also displays the picker's profile photo.
    private void setupPickerName(final GameMetadata game) {
        FirebaseUserResourceManager.getUserMetadataById(game.getPickerId(), new ResourceListener<UserMetadata>() {
            @Override
            public void onData(UserMetadata user) {
                if (user != null) {
                    pickerName.setText(user.getDisplayName());
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(), pickerPhoto);
                    pickerPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ProfileActivity.getProfileActivityCreator(GameActivity.this).create(game.getPickerId());
                        }
                    });
                }
            }

            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        });
    }

    // Downloads the possible cards for captions,
    // and sets up the recycler view for once the cards are downloaded
    private void setupCaptionCardView(GameMetadata gameMetadata) {
        // Sees if user clicks check button
        editCaptionText.setOnEditorActionListener(enterListener);
        // Setup recycler view
        LinearLayoutManager gameViewManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        captionCardsList.setLayoutManager(gameViewManager);
        cardListAdapter = new CardOptionsAdapter(new ArrayList<Card>(), new CardToTextConverter());
        captionCardsList.setAdapter(cardListAdapter);
        if (gameMetadata.isOpen()) {
            captionCardsList.addOnScrollListener(scrollFabHider);
        }
        populateCards(Constants.DEFAULT_PACK);
    }

    private void startCommentManager(GameMetadata game) {
        commentManager = new FirebaseResourceManager();
        String gameCaptionsPath = game.getIsPublic() ?
                String.format(GAME_PUBLIC_DATA_CAPTIONS_PATH, game.getId()) :
                String.format(GAME_PRIVATE_DATA_CAPTIONS_PATH, game.getId());
        commentManager.addChildListener(gameCaptionsPath, captionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commentManager != null && joinedGameManager != null) {
            commentManager.removeListener();
            joinedGameManager.removeListener();
        }
    }

    @OnClick (R.id.image_view)
    public void onClickGameImage() {
        Intent photoZoomIntent = new Intent(this, PhotoZoomActivity.class);
        photoZoomIntent.putExtra(PhotoZoomActivity.PHOTO_PATH, game.getImagePath());
        photoZoomIntent.putExtra(PhotoZoomActivity.TRANSITION_NAME, game.getId());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                imageView, game.getId());
        startActivity(photoZoomIntent, options.toBundle());
    }

    @OnClick(R.id.fab)
    public void displayCardOptions() {
        if (game.isOpen()) {
            //if the user is logged in they can caption
            if (FirebaseUserResourceManager.getUserId() != null) {
                toggleVisibility(captionCardsList);
                //If the card input is visible, want that hidden too. Don't necessarily want to toggle it.
                if (cardInputView.getVisibility() == View.VISIBLE) {
                    cardInputView.setVisibility(View.GONE);
                    // In case they press the fab while it's being hidden after scrolling
                    // This prevents it from being hidden forever.
                    hideKeyboard();
                }
            } else { //if they are logged out
                //display the loginDialog
                displayLoginDialog();
            }
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.end_date_passed_caption),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.join_game_button)
    public void joinGame() {
        if (FirebaseUserResourceManager.getUserId() == null) {
            loginDialog.show();
            return;
        }
        FirebaseUploader.addCurrentUserToGame(game, new ResourceListener<Exception>() {
            @Override
            public void onData(Exception data) {
                Snackbar.make(getCurrentFocus(), data.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public Class getDataType() {
                return Exception.class;
            }
        });
    }

    @OnClick(R.id.leave_button)
    public void leaveGame() {
        FirebaseUploader.removeCurrentUserFromGame(game, new ResourceListener<Exception>() {
            @Override
            public void onData(Exception data) {
                if(data.getLocalizedMessage() != null) {
                    Snackbar.make(getCurrentFocus(), data.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getCurrentFocus(), getString(R.string.problem_leaving), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
    }

    public void displayLoginDialog() {
        loginDialog.show();
    }

    private void initLoginManager() {
        loginDialog = new LoginDialog(this);
        //TODO: wrap the AuthCallbacks in a listener class so that we do not have to recreate
        //these callbacks every time we need to add in a login prompt in a new Activity
        loginManager = new LoginManager(this, new FirebaseUploader(), new LoginManager.LoginListener() {
            @Override
            public void onLoginComplete() {
                //probably do not need to do anything here
            }
            @Override
            public void onLogoutComplete() {
                //nothing needed here either
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //login was a success
                loginDialog.showPostLogDialog(getResources().getString(R.string.login_success));
                setupGameElements(game);
            }

            @Override
            public void onError() {
                //login was a failure
                loginDialog.showPostLogDialog(getResources().getString(R.string.login_failure));
            }
        }, new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                //logout was a success
                loginDialog.showPostLogDialog(getResources().getString(R.string.logout_success));
                setupGameElements(game);
            }

            @Override
            public void onError() {
                //logout was a failure
                loginDialog.showPostLogDialog(getResources().getString(R.string.logout_failure));
            }
        });
        loginDialog.setLoginManager(loginManager);
    }

    @OnClick(R.id.invite_friends)
    public void createGameInvite() {
        Bitmap bmp = BitmapConverter.drawableToBitmap(imageView.getDrawable());
        String sampleCaption = getSampleCaption();
        AddToGameDialog dlg = new AddToGameDialog(this, game, bmp, sampleCaption);

        dlg.show();
    }


    private String getSampleCaption() {
        Caption toReturn = game.getTopCaption();
        if (toReturn != null) {
            return toReturn.retrieveCaptionText().toString();
        } else {
            Random rand = new Random();
            return allCards.get(rand.nextInt(allCards.size() - 1)).getCardText().replace("%s", "____");
        }
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        setFabRotation();
    }

    private void setFabRotation() {
        float curRotation = fab.getRotation();
        // If the caption card list is visible, fab should be rotated to 135 deg. If it isn't,
        // then rotate it. and make it smaller
        if (captionCardsList.getVisibility() == View.VISIBLE) {
            if (curRotation != FAB_ROTATION) {
                ObjectAnimator.ofFloat(fab, "rotation",
                        0f, FAB_ROTATION).setDuration(ROTATION_TIME).start();
            }
        } else if (curRotation != 0) {
            // Rotate back to 0 if it's not already there and the card list is hidden
            ObjectAnimator.ofFloat(fab, "rotation",
                    FAB_ROTATION, 0f).setDuration(ROTATION_TIME).start();
        }
        // In case they click the fab too quick while scrolling in the caption cards, this will make
        // it not disappear forever
        if (game.isOpen()) {
            fab.show();
        }
    }

    public void submit(String userInput) {
        if (!userInput.isEmpty()) {
            editCaptionText.setText("");
            Uploader uploader = new FirebaseUploader();
            // Game will be a class variable probs
            Game game = this.game;
            addCaption(userInput, FirebaseUserResourceManager.getUserId(), uploader, curUserCard, game);
            toggleVisibility(cardInputView);
            toggleVisibility(captionCardsList);
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editCaptionText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId,
                                      KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String userInput = editCaptionText.getText().toString().trim();
                submit(userInput);
            }
            return true;
        }
    };

    /**
     * This class is used in the Adapter, and is called when a card is clicked.
     */
    class CardToTextConverter {
        void convertCard(Card curCard) {
            if (curCard.getId().equals(REFRESH_STRING)) {
                refreshCards();
            } else {
                curUserCard = curCard;
                displayCardInput(curCard.retrieveFirstHalfText(), curCard.retrieveSecondHalfText(),
                        curCard.getId().equals(BLANK_CARD));
            }
        }
    }

    private void displayCardInput(String firstHalfText, String secondHalfText, boolean customInput) {
        firstHalfCardText.setText(firstHalfText);
        secondHalfCardText.setText(secondHalfText);
        cardInputView.setVisibility(View.VISIBLE);
        if(customInput) {
            editCaptionText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_CUSTOM_LENGTH)});
        } else {
            // This is needed so if they click the custom one then a different one, the length resets
            editCaptionText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(MAX_PREFILLED_LENGTH)});
        }
        editCaptionText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editCaptionText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void refreshCards() {
        Random rand = new Random();
        // Add refresh card  is in the activity so that we can use the resource file. Easier to test
        handCards = addRefreshAndBlankCard(getRandomCardsFromList(allCards, rand));

        if (cardListAdapter != null) {
            cardListAdapter.replaceOptions(handCards);
        }
        captionCardsList.scrollToPosition(0);
    }

    private List<Card> addRefreshAndBlankCard(List<Card> cards) {
        Card refreshCard = new Card(getResources().getString(R.string.refresh));
        refreshCard.setId(REFRESH_STRING);
        Card blankCard = new Card("%s", BLANK_CARD);
        cards.add(blankCard);
        cards.add(refreshCard);
        return cards;
    }

    private void populateCards(String packName) {
        FirebaseResourceManager.loadCardsFromPack(packName,
                new ResourceListener<List<Card>>() {
                    @Override
                    public void onData(List<Card> data) {
                        allCards = data;
                        refreshCards();
                    }

                    @Override
                    public Class getDataType() {
                        return null; // Not used.
                    }
                });
    }

    /**
     * This is called after returning from a login intent from either Facebook or Google
     * This initiates the connection with firebase after contacting Facebook or Google
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if returning from google login attempt
        if (requestCode == GOOGLE_LOGIN_RC) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            loginManager.handleGoogleLoginResult(result);
        }
        //if returning from facebook login attempt
        else {
            loginManager.handleFacebookLoginResult(requestCode, resultCode, data);
        }
    }

}