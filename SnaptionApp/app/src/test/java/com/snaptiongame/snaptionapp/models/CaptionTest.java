package com.snaptiongame.snaptionapp.models;

/**
 * Testing the Caption class.
 * @author Cameron Geehr
 */

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Card;
import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.testobjects.TestCaption;
import com.snaptiongame.snaptionapp.testobjects.TestCard;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Tests the Caption class.
 *
 * @author Cameron Geehr
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
@SuppressStaticInitializationFor({"com.google.firebase.auth.FirebaseAuth"})
@PrepareForTest({FirebaseAuth.class})
public class CaptionTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private FirebaseAuth auth;

    @Mock
    private FirebaseUser user;

    @Before
    public void setup() {
        initMocks(this);
        mockStatic(FirebaseAuth.class);
        try {
            when(FirebaseAuth.class, "getInstance").thenReturn(auth);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testConstructTest() {
        TestCaption caption = new TestCaption();
        assertTrue(true);
    }

    @Test
    public void verifyConstructors() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = new Card("text");
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);
        Caption caption2 = new Caption("1", "2", "3", testCard1, userInput1);
        Caption caption = new Caption();

        assertEquals("1", caption1.getId());
        assertEquals("2", caption1.getGameId());
        assertEquals("3", caption1.getUserId());
        assertEquals(testCard1, caption1.getCard());
        assertEquals(userInput1, caption1.getUserInput());
        assertEquals(new HashMap<String, Integer>(), caption1.getVotes());
        //can't run retrieveCaptionText because SpannableStringBuilder can't be used in tests
        assertEquals(0, caption1.retrieveNumVotes());
        assertEquals(caption1.getId(), caption1.toString());
        assertTrue(caption1.equals(caption2));
        assertFalse(caption1.equals(null));
        assertFalse(caption1.equals(caption));

        List<String> userInput2 = new ArrayList<>();
        userInput1.add("8");
        userInput1.add("9");

        try {
            Caption caption3 = new Caption("6", "7", testCard1, userInput2);
            // Has to fail because not logged in
            fail();
        } catch (Throwable e) {}

    }

    @Test
    public void verifyUser() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);

        assertNull(caption1.retrieveUser());
        User user = new User("3", "something@something.com", "test", "5", "6", "image");
        caption1.assignUser(user);
        assertEquals(user, caption1.retrieveUser());
    }

    @Test
    public void verifyCompareTo() {
        List<String> userInput1 = new ArrayList<>();
        Card testCard1 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption1 = new Caption("1", "2", "3", testCard1, userInput1);
        Map<String, Integer> votes1 = new HashMap<>();
        votes1.put("1", 1);
        caption1.votes = votes1;

        assertEquals(1, caption1.retrieveNumVotes());

        List<String> userInput2 = new ArrayList<>();
        Card testCard2 = TestCard.getTestCardSingleInput();
        testCard1.setId("0");
        userInput1.add("4");
        userInput1.add("5");
        Caption caption2 = new Caption("2", "3", "4", testCard1, userInput1);
        caption2.votes = null;
        assertEquals(0, caption2.retrieveNumVotes());

        //test fewer votes
        assertTrue(caption1.compareTo(caption2) < 0);
        //test more votes
        assertTrue(caption2.compareTo(caption1) > 0);
        //test same votes with less id
        caption2.votes = votes1;
        assertTrue(caption1.compareTo(caption2) < 0);
        //test same votes with more id
        assertTrue(caption2.compareTo(caption1) > 0);
    }

    // Need to run this one as an instrumented test since Caption uses SpannableStringBuilder,
    // which is an Android resource
    @Test
    public void retrieveTwoInputCaptionTest() throws  Exception {
        ArrayList<String> inputArr = new ArrayList<String>();
        inputArr.add("Yay");
        inputArr.add("Cards work");
        String cardText = "%s! %s! I like ice cream";
        Card testCard = new Card(cardText);
        Caption testCaption = new Caption("TestId", "TestGameId", "Test user", testCard, inputArr);
        assertEquals("Yay! Cards work! I like ice cream", testCaption.retrieveCaptionText().toString());
    }

    @Test
    public void retrieveCardNoInputTest() {
        ArrayList<String> inputArr = new ArrayList<>();
        Card testCard = new Card("hello");
        Caption testCaption = new Caption("TestId", "TestGameId", "Test user", testCard, inputArr);

        assertEquals("hello", testCaption.retrieveCaptionText().toString());
    }

    @Test
    public void blankWhenNoCard() {
        Caption testCap = new Caption();
        assertEquals("", testCap.retrieveCaptionText().toString());
    }

    @Test
    public void testCaptionAuthValidation() {
        ArrayList<String> inputArr = new ArrayList<>();
        Card testCard = new Card("hello");
        when(user.getUid()).thenReturn("Uid");
        when(auth.getCurrentUser()).thenReturn(user);
        Caption testCap = new Caption("TestId", "TestGameId", testCard, inputArr);
        assertEquals("Uid", testCap.getUserId());
    }
}