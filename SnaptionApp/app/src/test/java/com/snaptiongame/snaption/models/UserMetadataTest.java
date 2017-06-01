package com.snaptiongame.snaption.models;

import org.junit.Before;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
/**
 * Created by austinrobarts on 4/22/17.
 */

public class UserMetadataTest {

    private UserMetadata emptyData;
    private UserMetadata nullData;
    private UserMetadata filledData;

    @Before
    public void setup() {
        emptyData = new UserMetadata();
        nullData = new UserMetadata(null, null, null, null, null, null);
        filledData = new UserMetadata("name", "email", "imagePath", "notificationId", "facebookId", "id");
    }

    @Test
    public void testConstructor() {
        //test empty constructor
        assertEquals(null, emptyData.getDisplayName());
        assertEquals(null, emptyData.getEmail());
        assertEquals(null, emptyData.getImagePath());
        assertEquals(null, emptyData.getNotificationId());
        assertEquals(null, emptyData.getFacebookId());
        assertEquals(null, emptyData.getId());
        //test null data
        assertEquals(null, nullData.getDisplayName());
        assertEquals(null, nullData.getEmail());
        assertEquals(null, nullData.getImagePath());
        assertEquals(null, nullData.getNotificationId());
        assertEquals(null, nullData.getFacebookId());
        assertEquals(null, nullData.getId());
        //test filled data
        assertEquals("name", filledData.getDisplayName());
        assertEquals("email", filledData.getEmail());
        assertEquals("imagePath", filledData.getImagePath());
        assertEquals("notificationId", filledData.getNotificationId());
        assertEquals("facebookId", filledData.getFacebookId());
        assertEquals("id", filledData.getId());

    }

    @Test
    public void testSetDisplayName() {
        assertEquals(null, nullData.getDisplayName());
        nullData.setDisplayName("name");
        assertEquals("name", nullData.getDisplayName());
    }

    @Test
    public void testSetEmail() {
        assertEquals(null, nullData.getEmail());
        nullData.setEmail("email");
        assertEquals("email", nullData.getEmail());
    }

    @Test
    public void testSetImagePath() {
        assertEquals(null, nullData.getImagePath());
        nullData.setImagePath("imagePath");
        assertEquals("imagePath", nullData.getImagePath());
    }

    @Test
    public void testSetNotificationId() {
        assertEquals(null, nullData.getNotificationId());
        nullData.setNotificationId("notificationId");
        assertEquals("notificationId", nullData.getNotificationId());
    }

    @Test
    public void testSetFacebookId() {
        assertEquals(null, nullData.getFacebookId());
        nullData.setFacebookId("facebookId");
        assertEquals("facebookId", nullData.getFacebookId());
    }

    @Test
    public void testSetId() {
        assertEquals(null, nullData.getId());
        nullData.setId("id");
        assertEquals("id", nullData.getId());
    }

    @Test
    public void testSetAndroid() {
        assertEquals(true, nullData.getIsAndroid());
        assertEquals(true, filledData.getIsAndroid());
        filledData.setIsAndroid(false);
        assertEquals(false, filledData.getIsAndroid());
    }

    @Test
    public void testSetSearch() {
        assertEquals("name", filledData.getDisplayName());
        filledData.setSearchName("notName");
        //setSearch should not change anything
        assertEquals("name", filledData.getSearchName());
        assertEquals("name", filledData.getDisplayName());
    }


}
