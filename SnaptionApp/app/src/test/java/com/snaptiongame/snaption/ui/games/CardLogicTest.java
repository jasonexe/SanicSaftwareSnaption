package com.snaptiongame.snaption.ui.games;

import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Card;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.servercalls.Uploader;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        Map<String, Integer> empty = new HashMap<>();
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
        Map<String, Integer> empty = new HashMap<>();
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
        Card card4 = new Card("card4", "4");
        Card card5 = new Card("card5", "5");
        Card card6 = new Card("card6", "6");
        Card card7 = new Card("card7", "7");
        Card card8 = new Card("card8", "8");
        Card card9 = new Card("card9", "9");
        Card card10 = new Card("card10", "10");

        randomCards.add(card1);
        randomCards.add(card2);
        randomCards.add(card3);
        randomCards.add(card4);
        randomCards.add(card5);
        randomCards.add(card6);
        randomCards.add(card7);
        randomCards.add(card8);
        randomCards.add(card9);
        randomCards.add(card10);

        List<Card> expectedList = new ArrayList<>();
        expectedList.add(card1);
        expectedList.add(card9);
        expectedList.add(card8);
        expectedList.add(card7);
        expectedList.add(card6);
        expectedList.add(card5);
        expectedList.add(card4);
        expectedList.add(card3);
        expectedList.add(card2);
        Random mockedRandom = mock(Random.class);
        when(mockedRandom.nextInt(randomCards.size()-1)).thenReturn(0)
                .thenReturn(8)
                .thenReturn(7)
                .thenReturn(6)
                .thenReturn(5)
                .thenReturn(4)
                .thenReturn(3)
                .thenReturn(5)
                .thenReturn(2)
                .thenReturn(8)
                .thenReturn(3)
                .thenReturn(1)
                .thenReturn(9);
        List actualList = CardLogic.getRandomCardsFromList(randomCards, mockedRandom);
        assertEquals(expectedList, actualList);
    }

}
