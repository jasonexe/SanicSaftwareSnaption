package com.snaptiongame.snaptionapp.ui.games;

import android.content.res.Resources;

import com.snaptiongame.snaptionapp.R;
import com.snaptiongame.snaptionapp.models.Card;

import java.util.List;
import java.util.Random;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.NUM_CARDS_IN_HAND;
import static com.snaptiongame.snaptionapp.ui.games.GameActivity.REFRESH_STRING;

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
}
