package com.snaptiongame.snaptionapp.ui.games;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.NUM_CARDS_IN_HAND;

/**
 * Does any card logic needed, used by GameActivity
 * Created by Krein on 2/7/2017.
 */

public class CardLogic {
    public static List<Card> getRandomCardsFromList(List<Card> allCards, Random rand) {
        List<Card> forHand;
        int randStart = rand.nextInt(allCards.size() - NUM_CARDS_IN_HAND - 1);
        forHand = allCards.subList(randStart, randStart + NUM_CARDS_IN_HAND);
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