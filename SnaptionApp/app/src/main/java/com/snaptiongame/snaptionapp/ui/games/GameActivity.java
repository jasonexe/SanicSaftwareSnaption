package com.snaptiongame.snaptionapp.ui.games;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.snaptiongame.snaptionapp.ui.games.CardLogic.addCaption;
import static com.snaptiongame.snaptionapp.ui.games.CardLogic.getRandomCardsFromList;

/**
 * This class is the core of the game screen, is in charge of basically all the UI-related, and some
 * logic-related Game code. This Activity is started when a user clicks on a photo on the wall
 * TODO needs to verify that a user is logged in before adding captions.
 * @Author Jason Krein, Cameron Geehr
 */
public class GameActivity extends AppCompatActivity {
    public static final String REFRESH_STRING = "refresh";
    private static final String DEFAULT_PACK = "InitialPack";
    private static final int ROTATION_TIME = 600;
    private static final float FAB_ROTATION = 135f;
    private List<Card> allCards = null;
    private List<Card> handCards = null;
    private Card curUserCard = null;
    private CardOptionsAdapter cardListAdapter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        ButterKnife.bind(this);
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
        toggleVisibility(captionCardsList);
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
        // TODO remove this when Cameron's code is merged in
        Game game = new Game("-Kbqjvc3cVKVPtcmTr6A", "", "", empty, empty, true, 0, 0, "mature");
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
                // TODO figure out why it won't pop up the
                // initial time something is selected
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
//        captionCardsList.smoothScrollToPosition(0);
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

}
