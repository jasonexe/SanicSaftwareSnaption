package com.snaptiongame.snaptionapp.models;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Captions will be contained inside of games. They keep track of their id, the id of the
 * user who made them, the id of the card they contain, the input the user gave, and the number
 * of upvotes that have been made
 * @author Jason Krein
 */

public class Caption implements Serializable {
    public String id;       // The id of the caption
    public String userId;   // The person who created the caption
    public String gameId;   // The game the caption was made on
    public Card card;       // The card that was used for this caption.
    public List<String> userInput; //List of user fill-ins for the blanks. Usually 1, could be more
    public Map<String, Integer> votes; // List of users who have upvoted this caption

    //Needed for firebase compatibility
    public Caption() {}

    // Used for dependency injection if you want a custom userId
    public Caption(String id, String gameId, String userId, Card card, List<String> userInput) {
        this.id = id;
        this.card = new Card(card);
        this.userInput = new ArrayList<>(userInput);
        this.userId = userId;
        this.gameId = gameId;
        votes = new HashMap<>();
    }

    public Caption(String id, String gameId, Card card, List<String> userInput) throws IllegalStateException{
        this.id = id;
        this.gameId = gameId;

        this.card = new Card(card);
        this.userInput = new ArrayList<>(userInput);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //If they're signed in, get their uid
            userId = user.getUid();
        } else {
            throw new IllegalStateException("User should be logged in when creating a caption");
        }
        votes = new HashMap<>();
    }

    public Card getCard() {
        return card;
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }

    public List<String> getUserInput() {
        return userInput;
    }

    public String getId() {
        return id;
    }

    public String getGameId() { return gameId; }

    public String getUserId() {
        return userId;
    }

    public int retrieveNumVotes() {
        if(votes == null) {
            return 0;
        } else {
            return votes.size();
        }
    }

    public SpannableStringBuilder retrieveCaptionText() {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(card == null) {
            return builder;
        }
        String finalString = card.getCardText();
        if(!finalString.contains("%s")) {
            return builder.append(finalString);
        }
        int firstModifier = 0;
        int lastModifier = 0;
        for(String userText : userInput) {
            firstModifier = finalString.indexOf("%s");
            builder.append(finalString.substring(lastModifier, firstModifier));
            // See where start of user text will be
            int startUnderline = builder.length();
            builder.append(userText);
            // This is where you set the styling for the user input.
            builder.setSpan(new UnderlineSpan(), startUnderline, builder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // Get rid of the %s is the reference string so the next indexOf will work
            finalString = finalString.substring(0, firstModifier) +
                    finalString.substring(firstModifier + 2);
            lastModifier = firstModifier;
        }
        builder.append(finalString.substring(firstModifier));
        return builder;
    }

    public boolean equals(Object other) {
        if(other.getClass() == this.getClass()) {
            Caption otherCap = (Caption) other;
            return this.getId().equals(otherCap.getId())
                    && this.getGameId().equals(otherCap.getGameId())
                    && this.getCard().equals(otherCap.getCard())
                    && this.getUserId().equals(otherCap.getUserId())
                    && this.getUserInput().equals(otherCap.getUserInput());
        }
        return false;
    }

    public String toString() {
        return this.getId();
    }
}
