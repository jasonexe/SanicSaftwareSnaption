package com.snaptiongame.snaptionapp.models;

/**
 * A Friend represents a potential of actual friend of the current user.
 */
public class Friend {
    public final String snaptionId;
    public final String displayName;
    public final String facebookId;

    /**
     * Constructor used to create an actual or potential friend from Facebook
     *
     * @param snaptionId String Snaption id
     * @param displayName String Snaption name
     * @param facebookId String unique Facebook id
     */
    public Friend(String snaptionId, String displayName, String facebookId) {
        this.snaptionId = snaptionId;
        this.displayName = displayName;
        this.facebookId = facebookId;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() == obj.getClass()) {
            Friend other = (Friend) obj;
            return snaptionId.equals(other.snaptionId) && displayName.equals(other.displayName) &&
                    facebookId.equals(other.facebookId);
        }
        return false;
    }
}
