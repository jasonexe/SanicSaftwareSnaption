package com.snaptiongame.snaption.testobjects;

import com.snaptiongame.snaption.models.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Will give you a card with
 */

public class TestCard {

    // These will only have one place for input.
    public static Card getTestCardSingleInput() {
        Random rand = new Random();
        List<String> someCardText = new ArrayList<>();
        someCardText.add("Hello! I like %s in my food");
        someCardText.add("%s is my fave movie");
        someCardText.add("Yesterday, I %s");
        someCardText.add("The right number of %s is 20");

        return new Card(someCardText.get(rand.nextInt(someCardText.size())));
    }
}
