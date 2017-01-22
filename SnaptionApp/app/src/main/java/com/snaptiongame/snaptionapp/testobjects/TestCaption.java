package com.snaptiongame.snaptionapp.testobjects;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

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

    public static Caption getDoubleInputCaption() {
        Random rand = new Random();
        List<List<String>> possibleInputs = new ArrayList<>();
        List<String> userInput = new ArrayList<>();
        userInput.add("eggs");
        userInput.add("patooty");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "Die Hard");
        userInput.set(1, "Christmas Movie");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "sleep for 10 hours");
        userInput.set(1, "can");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "peas");
        userInput.set(1, "not enough");
        possibleInputs.add(new ArrayList<String>(userInput));
        userInput.set(0, "between the sheets");
        userInput.set(1, "fortune cookie");
        possibleInputs.add(new ArrayList<String>(userInput));
        List<String> captionInput = possibleInputs.get(rand.nextInt(possibleInputs.size()));
        Card card = TestCard.getTestCardDoubleInput();

        return new Caption("TestId", "TestGameId", "TestUserId", card, captionInput);
    }
}
