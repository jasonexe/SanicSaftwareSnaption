package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;
import com.snaptiongame.snaption.models.UserPrivateData;
import com.snaptiongame.snaption.models.UserPublicData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by austinrobarts on 4/22/17.
 */

public class FirebaseUserResourceManager extends FirebaseResourceManager {

    /**
     * Gets the direct path to the user table in the database
     * @return a string path from the root node to current user
     */
    public static String getUserPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userPath = null;
        if (user != null)
            userPath = String.format(Constants.USER_PATH, user.getUid());
        return userPath;
    }

    /**
     * Gets the direct path to the user table in the database
     * @param id The ID of the user whose path to find
     * @return a string path from the root node to current user
     */
    public static String getUserPath(String id) {
        return String.format(Constants.USER_PATH, id);
    }

    /**
     * Get the user id of the current user
     * @return a string key to the user table
     */
    public static String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = null;
        if (user != null) {
            id = user.getUid();
        }
        return id;
    }


    public static void getUserById(final String id, final ResourceListener<User> listener) {
        //must get 3 pieces of data before sending whole user to callback
        getUserMetadataById(id, new ResourceListener<UserMetadata>() {
            @Override
            public void onData(final UserMetadata metadata) {
                getUserPublicDataById(id, new ResourceListener<UserPublicData>() {
                    @Override
                    public void onData(final UserPublicData publicData) {
                        getUserPrivateDataById(id, new ResourceListener<UserPrivateData>() {
                            @Override
                            public void onData(UserPrivateData privateData) {
                                listener.onData(new User(metadata, publicData, privateData));
                            }

                            @Override
                            public Class getDataType() {
                                return null;
                            }
                        });
                    }

                    @Override
                    public Class getDataType() {
                        return null;
                    }
                });
            }

            @Override
            public Class getDataType() {
                return null;
            }
        });
    }

    public static void getUserMetadataById(String id, ResourceListener<UserMetadata> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_METADATA_PATH, id), listener);
    }

    public static void getUserPublicDataById(String id, ResourceListener<UserPublicData> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_PUBLIC_DATA_PATH, id), listener);
    }

    public static void getUserPrivateDataById(String id, ResourceListener<UserPrivateData> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_PRIVATE_DATA_PATH, id), listener);
    }

    public static void getUserMetadataByFacebookId(String facebookId, ResourceListener<UserMetadata> listener) {
    }

    public static void getUsersByName(String begin, final ResourceListener<List<UserMetadata>> listener) {
        Query query = database.getReference(Constants.USERS_METADATA_PATH).orderByChild(Constants.SEARCH_NAME).startAt(begin).endAt(begin + "~");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<UserMetadata> users = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        users.add((UserMetadata) snapshot.getValue(listener.getDataType()));
                    }
                }
                listener.onData(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrieveUsersByName - " + databaseError.toString());
                listener.onData(null);
            }
        });
    }


}
