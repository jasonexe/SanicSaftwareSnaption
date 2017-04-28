package com.snaptiongame.snaption.models;

import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by austinrobarts on 2/16/17.
 */

public class UserTest {

    Map<String, Integer> friends;
    Map<String, Integer> games;
    Map<String, Caption> captions;
    Map<String, Integer> blockedUsers;
    Map<String, String> privateGames;

    User user1;
    User user2;
    User user3;
    User user4;
    User user5;
    User user6;
    User user7;

    @Before
    public void setup() {
        //init maps with data
        friends = new HashMap<>();
        friends.put("friend1", 1);
        friends.put("friend2", 1);
        games = new HashMap<>();
        games.put("game1", 1);
        games.put("game2", 1);
        captions = new HashMap<>();
        captions.put("caption1", new Caption());
        captions.put("caption2", new Caption());
        blockedUsers = new HashMap<>();
        blockedUsers.put("enemy1", 1);
        blockedUsers.put("enemy2", 1);
        privateGames = new HashMap<>();
        privateGames.put("game1", "private");
        privateGames.put("game2", "private");

        user1 = new User(new UserMetadata(), new UserPublicData(), new UserPrivateData());
        user2 = new User("id", "email", "displayName",
                "notificationId", "facebookId", "imagePath");
        user3 = new User("id", "email", "displayName",
                "notificationId", "facebookId", "imagePath",
                friends, games, captions, blockedUsers, privateGames);
        user4 = new User("id2", "email", "displayName",
                "notificationId", "facebookId", "imagePath");
        user5 = new User("id2", "email", "displayName",
                "notificationId", "facebookId", "imagePath");
        user6 = new User("id2", "email", "displayName2",
                "notificationId", "facebookId", "imagePath");
        user7 = new User("id2", "email2", "displayName",
                "notificationId", "facebookId", "imagePath");
    }

    @Test
    public void testEmptyConstructor() {
        assertEquals(null, user1.getId());
        assertEquals(null, user1.getEmail());
        assertEquals(null, user1.getDisplayName());
        assertEquals(null, user1.getNotificationId());
        assertEquals(null, user1.getFacebookId());
        assertEquals(null, user1.getImagePath());
        assertEquals(null, user1.getCreatedPrivateGames());
        assertEquals(null, user1.getCreatedPublicGames());
        assertEquals(null, user1.getJoinedGames());
        assertEquals(null, user1.getPrivateCaptions());
        assertEquals(null, user1.getPublicCaptions());
    }

    @Test
    public void testConstructorOne() {
        assertEquals("id", user2.getId());
        assertEquals("email", user2.getEmail());
        assertEquals("displayName", user2.getDisplayName());
        assertEquals("notificationId", user2.getNotificationId());
        assertEquals("facebookId", user2.getFacebookId());
        assertEquals("imagePath", user2.getImagePath());
        assertEquals(null, user1.getCreatedPrivateGames());
        assertEquals(null, user1.getCreatedPublicGames());
        assertEquals(null, user1.getJoinedGames());
        assertEquals(null, user1.getPrivateCaptions());
        assertEquals(null, user1.getPublicCaptions());
        assertEquals(null, user2.getFriends());
    }

    @Test
    public void testConstructorTwo() {
        //check String variables for correct data
        assertEquals("id", user3.getId());
        assertEquals("email", user3.getEmail());
        assertEquals("displayName", user3.getDisplayName());
        assertEquals("notificationId", user3.getNotificationId());
        assertEquals("facebookId", user3.getFacebookId());
        assertEquals("imagePath", user3.getImagePath());

        //check maps for correct data
        assertTrue(user3.getCreatedPrivateGames().containsKey("game1"));
        assertTrue(user3.getCreatedPrivateGames().containsKey("game2"));
        assertTrue(user3.getCreatedPublicGames().containsKey("game1"));
        assertTrue(user3.getCreatedPublicGames().containsKey("game2"));
        assertTrue(user3.getJoinedGames().containsKey("game1"));
        assertTrue(user3.getJoinedGames().containsKey("game2"));
        assertTrue(user3.getFriends().containsKey("friend1"));
        assertTrue(user3.getFriends().containsKey("friend2"));
        assertTrue(user3.getPublicCaptions().containsKey("caption1"));
        assertTrue(user3.getPrivateCaptions().containsKey("caption1"));
        assertTrue(user3.getPublicCaptions().containsKey("caption2"));
        assertTrue(user3.getPrivateCaptions().containsKey("caption2"));
    }

    @Test
    public void testRetrieveFriendsCount() {
        assertEquals(0, user1.retrieveFriendsCount());
        assertEquals(2, user3.retrieveFriendsCount());
    }

    @Test
    public void testRetrieveCaptionCount() {
        assertEquals(0, user1.retrieveCaptionCount());
        assertEquals(2, user3.retrieveCaptionCount());
        assertEquals(2, user3.retrieveCaptionCount());
    }

    @Test
    public void testRetrieveCreatedGameCount() {
        assertEquals(0, user1.retrieveCreatedGameCount());
        assertEquals(2, user3.retrieveCreatedGameCount());
        assertEquals(2, user3.retrieveCreatedGameCount());
    }

    @Test
    public void testLowercase() {
        assertEquals(null, user1.getLowercaseDisplayName());
        assertEquals("displayname", user2.getLowercaseDisplayName());
        assertEquals("displayname", user3.getLowercaseDisplayName());
        user2.setDisplayName("HELLO MOTO");
        assertEquals("hello moto", user2.getLowercaseDisplayName());
    }

    @Test
    public void testEquals() {
        assertEquals(user4, user5);
        assertFalse(user2.equals(user4));
        assertFalse(user4.equals(null));
        assertFalse(user4.equals(new Object()));
    }

    @Test
    public void testCompareTo() {
        assertEquals(0, user4.compareTo(user5));
        assertEquals(-1, user4.compareTo(user6));
        assertEquals(-1, user4.compareTo(user7));
        assertEquals(-1, user7.compareTo(user6));
        assertEquals(1, user7.compareTo(user4));
    }

    @Test
    public void testIsAndroid() {
        assertEquals(false, user1.getIsAndroid());
        assertEquals(true, user2.getIsAndroid());
        assertEquals(true, user3.getIsAndroid());
    }
}
