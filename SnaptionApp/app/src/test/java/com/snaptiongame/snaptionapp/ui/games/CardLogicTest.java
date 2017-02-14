package com.snaptiongame.snaptionapp.ui.games;

import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.snaptiongame.snaptionapp.ui.games.CardLogic.getRandomCardsFromList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jason_000 on 2/7/2017.
 */

public class CardLogicTest {

    @Test
    public void testAddCaption() {
        List<String> empty = new ArrayList<>();
        Map<String, Integer> emptyMap = new HashMap<>();
        String gameId = "-Kbqjvc3cVKVPtcmTr6A";
        String captionId = "testCapId";
        String userInput = "Test Input";
        String userId = "testUserId";
        String cardText = "testCard %s";
        String cardId = "testCardId";
        Game game = new Game(gameId, "", "", emptyMap, empty, true, 0, 0, "mature");
        List<String> expectedInput = new ArrayList<>();
        Card expectedCard = new Card(cardText, cardId);
        expectedInput.add(userInput);
        Caption correctCaption = new Caption(captionId, gameId, userId,
                expectedCard, expectedInput);

        Uploader mockedUploader = mock(Uploader.class);
        when(mockedUploader.getNewCaptionKey(gameId)).thenReturn(captionId);
        CardLogic.addCaption(userInput, userId, mockedUploader, expectedCard, game);

        verify(mockedUploader).addCaptions(correctCaption);
    }

    @Test
    public void testAddCaptionNoCard() {
        Uploader mockedUploader = mock(Uploader.class);
        List<String> empty = new ArrayList<>();
        Map<String, Integer> emptyMap = new HashMap<>();
        String gameId = "-Kbqjvc3cVKVPtcmTr6A";
        String userInput = "Test Input";
        String userId = "testUserId";
        Game game = new Game(gameId, "", "", emptyMap, empty, true, 0, 0, "mature");
        List<String> expectedInput = new ArrayList<>();
        Card expectedCard = null;
        expectedInput.add(userInput);

        CardLogic.addCaption(userInput, userId, mockedUploader, expectedCard, game);
        verify(mockedUploader, never()).addCaptions(null);
    }

    @Test
    public void testGetCardsFromList() {
        List<Card> randomCards = new ArrayList<>();
        Card card1 = new Card("card1", "1");
        Card card2 = new Card("card2", "2");
        Card card3 = new Card("card3", "3");

        randomCards.add(card1);
        randomCards.add(card2);
        randomCards.add(card2);
        randomCards.add(card2);
        randomCards.add(card2);
        randomCards.add(card3);
        randomCards.add(card2);
        randomCards.add(card1);
        randomCards.add(card3);
        randomCards.add(card3);
        randomCards.add(card3);
        randomCards.add(card1);
        randomCards.add(card1);
        randomCards.add(card1);
        randomCards.add(card2);
        randomCards.add(card2);
        randomCards.add(card2);
        randomCards.add(card2);

        List<Card> expectedList = randomCards.subList(0, 10);
        Random mockedRandom = mock(Random.class);
        when(mockedRandom.nextInt(randomCards.size())).thenReturn(0);
        List actualList = CardLogic.getRandomCardsFromList(randomCards, mockedRandom);
        assertEquals(expectedList, actualList);
    }

}
