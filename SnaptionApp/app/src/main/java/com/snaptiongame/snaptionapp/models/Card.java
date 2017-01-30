package com.snaptiongame.snaptionapp.models;

/**
 * Class to keep track of card info. When populating cardText, any blanks should be reperesented
 * by a %s modifier (I think modifier is the right word)
 * @author Jason Krein
 */

public class Card {
    public String id;
    public String cardText;

    /**
     * Default empty constructor required by Firebase
     */
    public Card() {}

    public Card (String cardText) {
        this.cardText = cardText;
    }

    public Card (Card oldCard) {
        this.cardText = oldCard.getCardText();
    }

    // Call this after firebase generates the key that the card will have.
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCardText() {
        return cardText;
    }
}
