package com.snaptiongame.snaptionapp.ui.captions;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
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
    private static final String CARD_OBJECT = "card_obj";
    private PopupMenu poppedUp = null;
    private List<Card> allCards = null;
    private boolean isRefreshing = false;
    private Card curUserCard;

    @BindView(R.id.firstHalfText)
    public TextView firstHalfCardText;

    @BindView(R.id.secondHalfText)
    public TextView secondHalfCardText;

    @BindView(R.id.editCaptionText)
    public EditText captionTextEntry;

    @BindView(R.id.card_input)
    public View cardInputView;


    TextView.OnEditorActionListener enterListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                System.out.println("Adding the caption");
                addCaption(captionTextEntry.getText().toString());
            }
            return true;
        }
    };

    private void addCaption(String userInput) {
        // Should never be null, but ya can't be too sure
        if (curUserCard != null) {
            Uploader uploader = new FirebaseUploader();
            // TODO replace this with the ID of the actual game.
            String gameId = "-Kbqjvc3cVKVPtcmTr6A";
            String captionId = uploader.getNewCaptionKey(gameId);
            List<String> allInput = new ArrayList<>();
            allInput.add(userInput);
            Caption userCaption = new Caption(captionId, gameId, curUserCard, allInput);
            uploader.addCaptions(userCaption);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caption);
        populateCards();
        ButterKnife.bind(this);
        captionTextEntry.setOnEditorActionListener(enterListener);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            // This will pop up the caption options
            @Override
            public void onClick(View view) {
                createCardMenu();

            }
        });
    }



    private void createCardMenu() {
        if(poppedUp != null) {
            poppedUp.dismiss();
        }
        // After the popup is dismissed, refreshing will definitely be false.
        // Make sure to set it after dismiss, otherwise if "refresh" is clicked, the
        // menu pops up in the wrong spot
        isRefreshing = false;
        View fab = findViewById(R.id.fab);
        fab.setRotation(0f);
        // Rotate the fab counter-clockwise 135 degrees when popping up the cards
        ObjectAnimator.ofFloat(fab, "rotation", 0f, rotationDeg).setDuration(ROTATION_TIME).start();
        Random rand = new Random();
        int randStart = rand.nextInt(allCards.size() - NUM_CARDS_IN_HAND - 1);
        List<Card> handCards = allCards.subList(randStart, randStart + NUM_CARDS_IN_HAND);
        poppedUp = showCardSelectPopup(fab, handCards);
        poppedUp.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // Make sure to rotate it back when the popup is closed if it's not being refreshed
                if(!isRefreshing) {
                    ObjectAnimator.ofFloat(findViewById(R.id.fab), "rotation",
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
            for (Card curCard : handCards) {
                MenuItem thisItem = cardMenu.add(curCard.getCardText().replace("%s", "______"));
                Intent cardIntent = new Intent();
                cardIntent.putExtra(CARD_OBJECT, curCard);
                thisItem.setIntent(cardIntent);
                thisItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Card clickedCard = (Card) menuItem.getIntent()
                                .getSerializableExtra(CARD_OBJECT);
                        curUserCard = clickedCard;
                        firstHalfCardText.setText(clickedCard.retrieveFirstHalfText());
                        secondHalfCardText.setText(clickedCard.retrieveSecondHalfText());
                        cardInputView.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
            }
            // TODO how to add an icon?
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
