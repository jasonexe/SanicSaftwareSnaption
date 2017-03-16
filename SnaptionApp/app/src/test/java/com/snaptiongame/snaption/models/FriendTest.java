package com.snaptiongame.snaption.models;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by jason_000 on 3/13/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", sdk = 21)
@PowerMockIgnore({ "org.mockito.*", "org.robolectric.*", "android.*" })
public class FriendTest {
    private String userId = "userId";
    private String userName = "name";
    private String email = "email";
    private String fbId = "123";
    private String notifyId = "notifyId";
    private String imagePath = "imagePath";

    @Test
    public void testCustomConstructor() {
        Friend customFriend = new Friend(userId, userName, email, fbId);
        assertEquals(userId, customFriend.getId());
        assertEquals(userName, customFriend.getDisplayName());
        assertEquals(email, customFriend.getEmail());
        assertEquals(fbId, customFriend.getFacebookId());
        assertNull(customFriend.getImagePath());
    }

    @Test
    public void testUserConstructor() {
        User createFrom = new User(userId, email, userName, notifyId, fbId, imagePath);
        Friend createdFriend = new Friend(createFrom);
        assertEquals(userId, createdFriend.getId());
        assertEquals(userName, createdFriend.getDisplayName());
        assertEquals(email, createdFriend.getEmail());
        assertEquals(fbId, createdFriend.getFacebookId());
        assertNull(createdFriend.getImagePath());
    }

    @Test
    public void testEqualsTrue() {
        Friend customFriend = new Friend(userId, userName, email, fbId);
        User createFrom = new User(userId, email, userName, notifyId, fbId, imagePath);
        Friend createdFriend = new Friend(createFrom);
        assertTrue(customFriend.equals(createdFriend));
    }

    @Test
    public void testEqualsFalse() {
        Friend customFriend = new Friend("BADID", userName, email, fbId);
        User createFrom = new User(userId, email, userName, notifyId, fbId, imagePath);
        Friend createdFriend = new Friend(createFrom);
        assertFalse(customFriend.equals(createdFriend));
    }

    @Test
    public void testEqualsNonFriend() {
        Friend customFriend = new Friend(userId, userName, email, fbId);
        assertFalse(customFriend.equals("False"));
    }
}
