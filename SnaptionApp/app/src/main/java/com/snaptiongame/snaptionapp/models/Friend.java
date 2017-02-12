package com.snaptiongame.snaptionapp.models;

/**
 * A Friend represents a potential of actual friend of the current user.
 */
public class Friend {
    public final String displayName;
    public final String facebookId;

    /**
     * Constructor used to create an actual or potential friend from Facebook
     * @param displayName String Snaption name
     * @param facebookId String unique Facebook id
     */
    public Friend(String displayName, String facebookId) {
        this.displayName = displayName;
        this.facebookId = facebookId;
    }
}
