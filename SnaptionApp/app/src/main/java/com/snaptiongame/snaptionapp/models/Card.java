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

    // Replaces the %s indicators with user input from the given list
    public String getCardWithUserInput(List<String> userInputs) {
        String finalString = cardText;
        // There may be a cleaner way to do this, but I couldn't figure out what to google.
        // Plus, it works.
        for(String userText : userInputs) {
            int firstModifier = finalString.indexOf("%s");
            finalString = finalString.substring(0, firstModifier) + userText +
                    finalString.substring(firstModifier + 2);
        }
        return finalString;
    }
}
