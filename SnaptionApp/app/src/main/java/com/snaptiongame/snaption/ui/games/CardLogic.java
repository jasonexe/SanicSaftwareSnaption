package com.snaptiongame.snaption.ui.games;

import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Card;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.servercalls.Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.snaptiongame.snaption.Constants.NUM_CARDS_IN_HAND;
import static com.snaptiongame.snaption.ui.games.GameActivity.BLANK_CARD;

/**
 * Does any card logic needed, used by GameActivity
 * Created by Krein on 2/7/2017.
 */

public class CardLogic {
    public static List<Card> getRandomCardsFromList(List<Card> allCards, Random rand) {
        List<Card> forHand = new ArrayList<>();
        for(int cardNum = 0; cardNum < NUM_CARDS_IN_HAND - 1; cardNum += 1) {
            Card potentialCard = allCards.get(rand.nextInt(allCards.size() - 1));
            if(!forHand.contains(potentialCard)) {
                forHand.add(potentialCard);
            } else {
                cardNum -= 1;
            }
        }
        Card blankCard = new Card("%s", BLANK_CARD);
        forHand.add(blankCard);
        return forHand;
    }

    // Creates the caption object, then uploads it to firebase using the uploader
    // Protected so we can test it
    public static void addCaption(String userInput, String userId, Uploader uploader,
                              Card curCard, Game game) {
        // Should never be null, but ya can't be too sure
        if (curCard != null) {
            String gameId = game.getId();
            String captionId = uploader.getNewCaptionKey(gameId);
            List<String> allInput = new ArrayList<>();
            allInput.add(userInput);
            Caption userCaption = new Caption(captionId, gameId,
                    userId, curCard, allInput);
            uploader.addCaptions(userCaption);
        }
    }
}
