package com.snaptiongame.snaption.testobjects;

import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Will make a test caption for you
 */

public class TestCaption {
    public static Caption getSingleInputCaption() {
        Random rand = new Random();
        List<List<String>> possibleInputs = new ArrayList<>();
        List<String> userInput = new ArrayList<>();
        userInput.add("eggs");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "Die Hard");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "sleep for 10 hours");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "peas");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "between the sheets");
        possibleInputs.add(new ArrayList<String>(userInput));
        List<String> captionInput = possibleInputs.get(rand.nextInt(possibleInputs.size()));
        Card card = TestCard.getTestCardSingleInput();

        return new Caption("TestId", "TestGameId", "TestUserId", card, captionInput);
    }
}
