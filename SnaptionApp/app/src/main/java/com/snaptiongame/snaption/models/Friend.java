package com.snaptiongame.snaption.models;

import android.text.TextUtils;

/**
 * A Friend represents a potential of actual friend of the current user.
 */
public class Friend implements Person {
    public final String snaptionId;
    public final String displayName;
    public final String facebookId;
    public final String email;

    /**
     * Constructor used to create an actual or potential friend from Facebook
     *
     * @param snaptionId String Snaption id
     * @param displayName String Snaption name
     * @param facebookId String unique Facebook id
     */
    public Friend(String snaptionId, String displayName, String email, String facebookId) {
        this.snaptionId = snaptionId;
        this.displayName = displayName;
        this.email = email;
        this.facebookId = facebookId;
    }

    /**
     * Constructor to create a friend from an existing Sanption user.
     *
     * @param user User to convert to a friend
     */
    public Friend(UserMetadata user) {
        this(user.getId(), user.getDisplayName(), user.getEmail(), user.getFacebookId());
    }

    @Override
    public String getId() {
        return snaptionId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFacebookId() {
        return facebookId;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            Friend other = (Friend) obj;
            return TextUtils.equals(snaptionId, other.snaptionId) &&
                    TextUtils.equals(displayName, other.displayName) &&
                    TextUtils.equals(facebookId, other.facebookId) &&
                    TextUtils.equals(email, other.email);
        }
        return false;
    }
}
