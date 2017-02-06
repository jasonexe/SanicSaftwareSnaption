package com.snaptiongame.snaptionapp.ui.captions;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager;
import com.snaptiongame.snaptionapp.servercalls.FirebaseUploader;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CaptionActivity extends AppCompatActivity {
    public static final int NUM_CARDS_IN_HAND = 5;
    private static final float rotationDeg = -135f;
    private static final int ROTATION_TIME = 600;
    private PopupMenu poppedUp = null;
    private List<Card> allCards = null;
    private List<Card> handCards = null;
    private boolean isRefreshing = false;
    private Card curUserCard;

    @BindView(R.id.first_half_text)
    public TextView firstHalfCardText;

    @BindView(R.id.second_half_text)
    public TextView secondHalfCardText;

    @BindView(R.id.edit_caption_text)
    public EditText captionTextEntry;

    @BindView(R.id.card_input)
    public View cardInputView;

    @BindView(R.id.submitCaptionButton)
    public Button submitButton;

    @BindView(R.id.fab)
    public FloatingActionButton fab;


    TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                addCaption();
            }
            return true;
        }
    };

    private void addCaption() {
        // Should never be null, but ya can't be too sure
        if (curUserCard != null) {
            String userInput = captionTextEntry.getText().toString();
            Uploader uploader = new FirebaseUploader();
            // TODO replace this with the ID of the actual game.
            String gameId = "-Kbqjvc3cVKVPtcmTr6A";
            String captionId = uploader.getNewCaptionKey(gameId);
            List<String> allInput = new ArrayList<>();
            allInput.add(userInput);
            Caption userCaption = new Caption(captionId, gameId, curUserCard, allInput);
            uploader.addCaptions(userCaption);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(captionTextEntry.getWindowToken(), 0);
            cardInputView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(captionTextEntry.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(captionTextEntry.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        populateCards();
        ButterKnife.bind(this);
        // Listen for if the user presses "enter." They can also submit by clicking the button
        captionTextEntry.setOnEditorActionListener(enterListener);

        fab.setOnClickListener(new View.OnClickListener() {
            // This will pop up the caption options
            @Override
            public void onClick(View view) {
                createCardMenu();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCaption();
            }
        });
    }



    private void createCardMenu() {
        if(poppedUp != null) {
            poppedUp.dismiss();
        }
        // If refresh was clicked or there are no hand cards, get a new hand
        if(isRefreshing || handCards == null) {
            Random rand = new Random();
            int randStart = rand.nextInt(allCards.size() - NUM_CARDS_IN_HAND - 1);
            handCards = allCards.subList(randStart, randStart + NUM_CARDS_IN_HAND);
        }
        // After the popup is dismissed, refreshing will definitely be false.
        // Make sure to set it after dismiss, otherwise if "refresh" is clicked, the
        // menu pops up in the wrong spot
        isRefreshing = false;
        fab.setRotation(0f);
        // Rotate the fab counter-clockwise 135 degrees when popping up the cards
        ObjectAnimator.ofFloat(fab, "rotation", 0f, rotationDeg).setDuration(ROTATION_TIME).start();
        poppedUp = showCardSelectPopup(fab, handCards);
        poppedUp.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Make sure to rotate it back when the popup is closed if it's not being refreshed
                if(!isRefreshing) {
                    ObjectAnimator.ofFloat(fab, "rotation",
                            rotationDeg, 0f).setDuration(ROTATION_TIME).start();
                }
            }
        });
    }

    private PopupMenu showCardSelectPopup(View v, List<Card> handCards) {
        Context wrapper = new ContextThemeWrapper(v.getContext(), R.style.CustomPopupStyle);
        PopupMenu popup = new PopupMenu(wrapper, v);
        MenuInflater inflater = popup.getMenuInflater();
        // Make sure cards list got populated
        if(allCards != null) {
            Menu cardMenu = popup.getMenu();
            for (final Card curCard : handCards) {
                MenuItem thisItem = cardMenu.add(curCard.getCardText().replace("%s", "______"));
                thisItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        curUserCard = curCard;
                        firstHalfCardText.setText(curCard.retrieveFirstHalfText());
                        secondHalfCardText.setText(curCard.retrieveSecondHalfText());
                        cardInputView.setVisibility(View.VISIBLE);
                        // TODO figure out why it won't pop up the initial time something is selected
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                                .showSoftInput(captionTextEntry, InputMethodManager.SHOW_FORCED);
                        return true;
                    }
                });
            }
            // TODO figure out how to add icon/if it's possible to add one
            MenuItem refreshItem = cardMenu.add("Refresh");
            refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    isRefreshing = true;
                    createCardMenu();
                    return true;
                }
            });

            inflater.inflate(R.menu.card_menu_options, cardMenu);
            popup.show();
        }
        return popup;
    }

    private void populateCards() {
        FirebaseResourceManager.loadCardsFromPack("InitialPack", new ResourceListener<List<Card>>() {
            @Override
            public void onData(List<Card> data) {
                allCards = data;
            }

            @Override
            public Class getDataType() {
                return null; // Not used.
            }
        });
    }

}
