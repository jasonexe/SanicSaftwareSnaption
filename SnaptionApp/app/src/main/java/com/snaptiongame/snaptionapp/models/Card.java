package com.snaptiongame.snaptionapp.models;

import java.util.List;

/**
 * Class to keep track of card info. When populating cardText, any blanks should be reperesented
 * by a %s modifier (I think modifier is the right word)
 * @author Jason Krein
 */

public class Card {
    public String id;
    public String cardText;

    public Card (String cardText) {
        this.cardText = cardText;
    }

    public String getId() {
        return id;
    }

    public String getCardText() {
        return cardText;
    }
}
