package com.snaptiongame.snaptionapp.ui.games;

import android.animation.ObjectAnimator;
import android.content.Context;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.LoginManager;
import com.snaptiongame.snaptionapp.servercalls.Uploader;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.ui.HomeAppCompatActivity;
import com.snaptiongame.snaptionapp.ui.login.LoginDialog;
import com.snaptiongame.snaptionapp.ui.wall.WallViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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

    private LoginManager loginManager;

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

    @BindView(R.id.submit_caption_button)
    public Button submitCaptionButton;

    @BindView(R.id.fab)
    public FloatingActionButton fab;

    @BindView(R.id.possible_caption_cards_list)
    public RecyclerView captionCardsList;

    private ResourceListener captionListener = new ResourceListener<Caption>() {
        @Override
        public void onData(Caption data) {
            if (data != null) {
                captionAdapter.addCaption(data);
            }
        }

        @Override
        public Class getDataType() {
            return Game.class;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        game = (Game)getIntent().getSerializableExtra(WallViewAdapter.GAME); //Obtaining data
        photoPath = game.getImagePath();
        FirebaseResourceManager.loadImageIntoView(photoPath, imageView);

        LinearLayoutManager captionViewManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        captionListView.setLayoutManager(captionViewManager);
        if (game.getCaptions() != null) {
            numberCaptions.setText(Integer.toString(game.getCaptions().size()));
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<>(game.getCaptions().values()));
        }
        else {
            captionAdapter = new GameCaptionViewAdapter(new ArrayList<Caption>());
            numberCaptions.setText(EMPTY_SIZE);
        }
        captionListView.setAdapter(captionAdapter);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(game.getEndDate());
        endDate.setText(new SimpleDateFormat("MM/dd/yy").format(calendar.getTime()));

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

        populateCards(DEFAULT_PACK);
        // Listen for if the user presses "enter."
        // They can also submit by clicking the button
        editCaptionText.setOnEditorActionListener(enterListener);
        // Setup recycler view
        LinearLayoutManager gameViewManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        captionCardsList.setLayoutManager(gameViewManager);
        cardListAdapter = new CardOptionsAdapter(new ArrayList<Card>(), new CardToTextConverter());
        captionCardsList.setAdapter(cardListAdapter);
    }

    @OnClick(R.id.fab)
    public void displayCardOptions() {
        //if the user is logged in they can caption
        if (FirebaseResourceManager.getUserId() != null) {
            toggleVisibility(captionCardsList);
            //If the card input is visible, want that hidden too. Don't necessarily want to toggle it.
            if(cardInputView.getVisibility() == View.VISIBLE) {
                cardInputView.setVisibility(View.GONE);
                hideKeyboard();
            }
        }
        else { //if they are logged out
            //display the loginDialog
            final LoginDialog dialog = new LoginDialog(this);
            loginManager = new LoginManager(this, new FirebaseUploader(), new LoginManager.LoginListener() {
                @Override
                public void onLoginComplete() {
                    //probably do not need to do anything here
                }
            }, new LoginManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    //login was a success
                    dialog.showPostLogDialog(getResources().getString(R.string.login_success));
                }
                @Override
                public void onError() {
                    //login was a failure
                    dialog.showPostLogDialog(getResources().getString(R.string.login_failure));
                }
            }, new LoginManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    //logout was a success
                    dialog.showPostLogDialog(getResources().getString(R.string.logout_success));
                }

                @Override
                public void onError() {
                    //logout was a failure
                    dialog.showPostLogDialog(getResources().getString(R.string.logout_failure));
                }
            });
            dialog.setLoginManager(loginManager);
            dialog.show();

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
        // then rotate it.
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
    }

    @OnClick(R.id.submit_caption_button)
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
