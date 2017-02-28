package com.snaptiongame.snaptionapp.ui.games;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseDeepLinkCreator;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.LoginManager;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.HomeAppCompatActivity;
import com.snaptiongame.snaptionapp.ui.ScrollFabHider;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;
import com.snaptiongame.snaptionapp.utilities.BitmapConverter;

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

import static com.snaptiongame.snaptionapp.servercalls.LoginManager.GOOGLE_LOGIN_RC;
import static com.snaptiongame.snaptionapp.ui.games.CardLogic.addCaption;
import static com.snaptiongame.snaptionapp.ui.games.CardLogic.getRandomCardsFromList;

/**
 * This class is the core of the game screen, is in charge of basically all the UI-related, and some
 * logic-related Game code. This Activity is started when a user clicks on a photo on the wall
 * TODO needs to verify that a user is logged in before adding captions.
 * @Author Jason Krein, Cameron Geehr
 */
public class GameActivity extends HomeAppCompatActivity {
    public static final String USE_GAME_ID = "useGameId";
    public static final String REFRESH_STRING = "refresh";
    private final static String DEFAULT_PACK = "InitialPack";
    private final static String GAME_DIRECTORY = "games";
    private final static String CAPTION_DIRECTORY = "captions";
    private final static String EMPTY_SIZE = "0";
    private static final int ROTATION_TIME = 600;
    private static final float FAB_ROTATION = 135f;
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

    @BindView(R.id.intent_load_progress)
    public View progressSpinner;

    private ResourceListener captionListener = new ResourceListener<Caption>() {
        @Override
        public void onData(Caption data) {
            if (data != null) {
                captionAdapter.addCaption(data);
                numberCaptions.setText(String.format(Locale.getDefault(),
                        "%d", captionAdapter.getItemCount()));
            }
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

        Intent startedIntent = getIntent();
        // If started from the wall, we'll have been sent the Game object, so can use that for stuff
        if(startedIntent.hasExtra(WallViewAdapter.GAME)) {
            game = (Game)getIntent().getSerializableExtra(WallViewAdapter.GAME); //Obtaining data
            setupGameElements(game);
        } else if(startedIntent.hasExtra(USE_GAME_ID)) {
            // If we were started via deep link, we'll only have the game ID. Have to pull
            // from firebase
            String gameId = startedIntent.getStringExtra(USE_GAME_ID);
            FirebaseResourceManager.retrieveSingleNoUpdates(GAME_DIRECTORY + "/" + gameId,
                    new ResourceListener<Game>() {
                        @Override
                        public void onData(Game data) {
                            if(data != null) {
                                setupGameElements(data);
                            } else {
                                System.err.println("Game activity was passed an incorrect gameId");
                            }
                        }

                        @Override
                        public Class getDataType() {
                            return Game.class;
                        }
                    });
        }
    }

    private void setupGameElements(Game game) {
        this.game = game;
        photoPath = game.getImagePath();
        FirebaseResourceManager.loadImageIntoView(photoPath, imageView);
        initLoginManager();
        setupButtonDisplay(game);
        setupCaptionList(game);
        setupEndDate(game);
        setupPickerName(game);
        setupCaptionCardView();
        startCommentManager(game);
    }

    private void setupButtonDisplay(Game game) {
        // When this is initially called, setup the button with current data
        final String pickerId = game.getPicker();
        determineButtonDisplay(pickerId, game.getPlayers().keySet());
        joinedGameManager = new FirebaseResourceManager();
        // setup a listener for when player joins the game
        joinedGameManager.retrieveMapWithUpdates(String.format(FirebaseUploader.GAME_PLAYERS_PATH,
                game.getId()), new ResourceListener<Map<String, Object>>() {
            @Override
            public void onData(Map<String, Object> data) {
                // retrieveMapWithUpdates guaranteed to return a map from string to object
                if(data != null) {
                    determineButtonDisplay(pickerId, data.keySet());
                }
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
    }

    private void determineButtonDisplay(String pickerId, Set<String> players) {
        String thisUser = FirebaseResourceManager.getUserId();
        // If they're not logged in, just show join game
        if(thisUser == null) {
            setJoinGameIsVisible(true);
            return;
        }
        boolean thisPlayerInGame = false;
        if(players != null && players.contains(thisUser)) {
            thisPlayerInGame = true;
        }
        // If this is a public game, anyone can send an invite to it if they've joined
        if(game.getIsPublic()) {
            // If they're in the list of players, they can invite
            // If they're not, show them the join button
            setJoinGameIsVisible(!thisPlayerInGame);
        } else {
            // If it's a private game and the picker is logged in, they can invite people
            if(pickerId.equals(thisUser)) {
                setJoinGameIsVisible(false);
            } else {
                // If they're not in the game, set join to visible. Otherwise, not visible
                setJoinGameIsVisible(!thisPlayerInGame);
                // If user logged in isn't the picker, no inviting for them!
                inviteFriendsButton.setVisibility(View.GONE);
            }
        }
    }

    private void setJoinGameIsVisible(boolean isVisible) {
        if(isVisible) {
            inviteFriendsButton.setVisibility(View.GONE);
            joinGameButton.setVisibility(View.VISIBLE);
        } else {
            inviteFriendsButton.setVisibility(View.VISIBLE);
            joinGameButton.setVisibility(View.GONE);
        }
    }

    private void setupCaptionList(Game game) {
        LinearLayoutManager captionViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        captionListView.setLayoutManager(captionViewManager);
        if (game.getCaptions() != null) {
            numberCaptions.setText(Integer.toString(game.getCaptions().size()));
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<>(game.getCaptions().values()), loginDialog);
        }
        else {
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<Caption>(), loginDialog);
            numberCaptions.setText(EMPTY_SIZE);
        }
        captionListView.setAdapter(captionAdapter);
        scrollFabHider = new ScrollFabHider(fab, ScrollFabHider.BIG_HIDE_THRESHOLD);
        captionListView.addOnScrollListener(scrollFabHider);
    }

    // Displays the date that the game will end underneath the picture
    private void setupEndDate(Game game) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(game.getEndDate());
        endDate.setText(new SimpleDateFormat("MM/dd/yy", Locale.getDefault())
                .format(calendar.getTime()));
    }

    // Displays the name of the picture underneath the picture, and
    // also displays the picker's profile photo.
    private void setupPickerName(Game game) {
        String userPath = FirebaseResourceManager.getUserPath(game.getPicker());
        FirebaseResourceManager.retrieveSingleNoUpdates(userPath, new ResourceListener<User>() {
            @Override
            public void onData(User user) {
                if (user != null) {
                    pickerName.setText(user.getDisplayName());
                    FirebaseResourceManager.loadImageIntoView(user.getImagePath(), pickerPhoto);
                }
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    // Downloads the possible cards for captions,
    // and sets up the recycler view for once the cards are downloaded
    private void setupCaptionCardView() {
        // Sees if user clicks check button
        editCaptionText.setOnEditorActionListener(enterListener);
        // Setup recycler view
        LinearLayoutManager gameViewManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        captionCardsList.setLayoutManager(gameViewManager);
        cardListAdapter = new CardOptionsAdapter(new ArrayList<Card>(), new CardToTextConverter());
        captionCardsList.setAdapter(cardListAdapter);
        captionCardsList.addOnScrollListener(scrollFabHider);
        populateCards(DEFAULT_PACK);
    }

    private void startCommentManager(Game game) {
        commentManager = new FirebaseResourceManager();
        commentManager.addChildListener(GAME_DIRECTORY + "/" + game.getId() + "/" + CAPTION_DIRECTORY,
                captionListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        commentManager.removeListener();
        joinedGameManager.removeListener();
    }

    @OnClick(R.id.fab)
    public void displayCardOptions() {
        //if the user is logged in they can caption
        if (FirebaseResourceManager.getUserId() != null) {
            toggleVisibility(captionCardsList);
            //If the card input is visible, want that hidden too. Don't necessarily want to toggle it.
            if(cardInputView.getVisibility() == View.VISIBLE) {
                cardInputView.setVisibility(View.GONE);
                // In case they press the fab while it's being hidden after scrolling
                // This prevents it from being hidden forever.
                hideKeyboard();
            }
        }
        else { //if they are logged out
            //display the loginDialog
            displayLoginDialog();
        }
    }

    @OnClick(R.id.join_game_button)
    public void joinGame() {
        if(FirebaseResourceManager.getUserId() == null) {
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
        FirebaseDeepLinkCreator.createGameInviteIntent(this, game, progressSpinner, bmp, sampleCaption);
    }

    private String getSampleCaption() {
        Caption toReturn = game.getTopCaption();
        if(toReturn != null) {
            return toReturn.retrieveCaptionText().toString();
        } else {
            Random rand = new Random();
            return allCards.get(rand.nextInt(allCards.size() - 1)).getCardText().replace("%s", "____");
        }
    }

    private void toggleVisibility(View view) {
        if(view.getVisibility() == View.VISIBLE) {
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
        if(captionCardsList.getVisibility() == View.VISIBLE) {
            if(curRotation != FAB_ROTATION) {
                ObjectAnimator.ofFloat(fab, "rotation",
                        0f, FAB_ROTATION).setDuration(ROTATION_TIME).start();
            }
        } else if(curRotation != 0) {
            // Rotate back to 0 if it's not already there and the card list is hidden
            ObjectAnimator.ofFloat(fab, "rotation",
                    FAB_ROTATION, 0f).setDuration(ROTATION_TIME).start();
        }
        // In case they click the fab too quick while scrolling in the caption cards, this will make
        // it not disappear forever
        fab.show();
    }

    public void submit() {
        String userInput = editCaptionText.getText().toString();
        editCaptionText.setText("");
        Uploader uploader = new FirebaseUploader();
        // Game will be a class variable probs
        List<String> empty = new ArrayList<>();
        Game game = this.game;
        addCaption(userInput, FirebaseResourceManager.getUserId(), uploader, curUserCard, game);
        toggleVisibility(cardInputView);
        toggleVisibility(captionCardsList);
        hideKeyboard();
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
                submit();
            }
            return true;
        }
    };

    /**
     * This class is used in the Adapter, and is called when a card is clicked.
     */
    class CardToTextConverter {
        public void convertCard(Card curCard) {
            if (curCard.getId().equals(REFRESH_STRING)) {
                refreshCards();
            } else {
                curUserCard = curCard;
                firstHalfCardText.setText(
                        curCard.retrieveFirstHalfText());
                secondHalfCardText.setText(
                        curCard.retrieveSecondHalfText());
                cardInputView.setVisibility(View.VISIBLE);
                editCaptionText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editCaptionText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void refreshCards() {
        Random rand = new Random();
        // Add refresh card  is in the activity so that we can use the resource file. Easier to test
        handCards = addRefreshCard(getRandomCardsFromList(allCards, rand));

        if (cardListAdapter != null) {
            cardListAdapter.replaceOptions(handCards);
        }
        captionCardsList.scrollToPosition(0);
    }

    private List<Card> addRefreshCard(List<Card> cards) {
        Card refreshCard = new Card(getResources().getString(R.string.refresh));
        refreshCard.setId(REFRESH_STRING);
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
