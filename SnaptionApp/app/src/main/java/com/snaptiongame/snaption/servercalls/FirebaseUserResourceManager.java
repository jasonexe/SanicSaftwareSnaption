package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.snaptiongame.snaption.models.Friend;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ResourceManager responsible for fetching any data related to a user from Firebase
 * Created by austinrobarts on 4/22/17.
 */
public class FirebaseUserResourceManager extends FirebaseResourceManager {
    private static final String FB_REQUEST_DATA = "data";
    private static final String FB_REQUEST_ID = "id";
    private FirebaseResourceManager firebase;

    public FirebaseUserResourceManager() {
        firebase = new FirebaseResourceManager();
    }

    /**
     * Gets the direct path to the user metadata table in the database
     * @return a string path from the root node to current user
     */
    public static String getUserMetadataPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userPath = null;
        if (user != null)
            userPath = String.format(Constants.USER_METADATA_PATH, user.getUid());
        return userPath;
    }

    public static boolean isValidUser(String id) {
        String path = getUserPath(id);
        return validFirebasePath(path);
    }

    /**
     * Gets the direct path to the user metadata table in the database
     * @param id The ID of the user whose path to find
     * @return a string path from the root node to current user
     */
    public static String getUserPath(String id) {
        return String.format(Constants.USER_METADATA_PATH, id);
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

    /**
     * Gets the list of providers that the current user's account is associated with.
     * @return the list of providers the user's account is associated with
     */
    public static List<String> getProviders() {
        List<String> providers = new ArrayList<String>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            providers = currentUser.getProviders();
        }
        return providers;
    }

    /**
     * Get the entire user object from firebase based on their uid.
     * DO NOT use this call if you only want a certain piece of data from the user
     * @param id the id of the user to fetch
     * @param listener the listener to receive the User once it is pulled
     */
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
                                return UserPrivateData.class;
                            }
                        });
                    }

                    @Override
                    public Class getDataType() {
                        return UserPublicData.class;
                    }
                });
            }

            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        });
    }

    /**
     * Get the metadata of a user based on id. This contains data such as displayName, profile image
     * path, and email.
     * @param id uid of user to retrieve
     * @param listener the receiver of the user metadata once it is pulled
     */
    public static void getUserMetadataById(String id, ResourceListener<UserMetadata> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_METADATA_PATH, id), listener);
    }

    /**
     * Loads a map of users. This will most often be used to retrieve the friends of a user
     *
     * @param uids Map of user ids
     * @param listener ResourceListener the users are returned to
     */
    public static void getUsersMetadataByIds(Map<String, Integer> uids, ResourceListener<UserMetadata> listener) {
        for (String uid : uids.keySet()) {
            String friend = String.format(Constants.USER_METADATA_PATH, uid);
            // ensure the user id is a valid one to avoid errors
            if (validFirebasePath(friend)) {
                getUserMetadataById(uid, listener);
            }
        }
    }

    /**
     * Get the public of a user based on id. This contains data such as friends, public created games,
     * and public captioned games
     * @param id uid of user to retrieve
     * @param listener the receiver of the user metadata once it is pulled
     */
    public static void getUserPublicDataById(String id, ResourceListener<UserPublicData> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_PUBLIC_DATA_PATH, id), listener);
    }

    public static void getUserFriends(String id, ResourceListener<Map<String, Integer>> listener) {
        retrieveStringMapNoUpdates(String.format(Constants.USER_FRIENDS_PATH, id), listener);
    }

    public void getUserFriendsWithUpdates(String id, ResourceListener<Map<String, Integer>> listener) {
        firebase.retrieveMapWithUpdates(String.format(Constants.USER_FRIENDS_PATH, id), listener);
    }

    /**
     * Get the private data of a user based on id. This contains data such as private created games,
     * private captions, and joined games
     * @param id uid of user to retrieve
     * @param listener the receiver of the user metadata once it is pulled
     */
    public static void getUserPrivateDataById(String id, ResourceListener<UserPrivateData> listener) {
        retrieveSingleNoUpdates(String.format(Constants.USER_PRIVATE_DATA_PATH, id), listener);
    }

    /**
     * Retrieves the Snaption User associated with the given Facebook unique id, and returns the
     * User to the given ResourceListener
     *
     * @param facebookId String unique Facebook id of a Facebook user
     * @param listener ResourceListener the user is returned to
     */
    public static void getUserMetadataByFacebookId(final String facebookId, final ResourceListener<UserMetadata> listener) {
        Query query = database.getReference(Constants.USER_METADATA_PATH).orderByChild(Constants.USER_FACEBOOK_ID)
                .equalTo(facebookId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Iterator<DataSnapshot> facebookUserIterator = dataSnapshot.getChildren().iterator();
                    if (facebookUserIterator.hasNext()) {
                        UserMetadata user = facebookUserIterator.next().getValue(UserMetadata.class);
                        listener.onData(user);
                    }
                    else {
                        listener.onData(null);
                    }
                } catch (Exception err) {
                    FirebaseReporter.reportException(err, "Failed to get user by facebook id: " + facebookId);
                    listener.onData(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onData(null);
            }
        });
    }

    /**
     * Loads a list of users based on the start of their display name and/or e-mail.
     *
     * @param begin the name/e-mail to be searched for
     * @param listener ResourceListener the users are returned to
     */
    public static void getUserMetadataByName(String begin, String searchType, final ResourceListener<List<UserMetadata>> listener) {
        Query query = database.getReference(Constants.USERS_METADATA_PATH).orderByChild(searchType).startAt(begin).endAt(begin + "~");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    List<UserMetadata> users = new ArrayList<>();
                    Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                    if (snapshots.iterator().hasNext()) {
                        for (DataSnapshot snapshot : snapshots) {
                            users.add((UserMetadata) snapshot.getValue(listener.getDataType()));
                        }
                    }
                    listener.onData(users);
                }
                catch(Exception err) {
                    FirebaseReporter.reportException(err, "Failed trying to get users by name");
                    listener.onData(null);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrieveUsersByName - " + databaseError.toString());
                listener.onData(null);
            }
        });
    }

    /**
     * Retrieves a list of Friends, representing the Facebook friends that have logged
     * into Snaption of the given User, and returns the list of
     * Friends to the given ResourceListener. The friendsFilter is used to filter out all users
     * with the specified Snaption ids.
     *
     * @param user Facebook user
     * @param friendsFilter Snaption ids of friends that should not be returned
     * @param friendListener ResourceListener the list of Friends is returned to
     */
    public static void getFacebookFriends(final UserMetadata user, final Map<String, Integer> friendsFilter,
                                          final ResourceListener<Friend> friendListener) {
        getFacebookFriends(user, new ResourceListener<Friend>() {
            @Override
            public void onData(Friend friend) {
                // filter out the friends
                if (friendsFilter == null || !friendsFilter.containsKey(friend.snaptionId)) {
                    friendListener.onData(friend);
                }
            }

            @Override
            public Class getDataType() {
                return Friend.class;
            }
        });
    }

    /**
     * Retrieves a list of Friends, representing the Facebook friends that have logged
     * into Snaption of the given User, and returns the list of
     * Friends to the given ResourceListener
     *
     * @param user Facebook user
     * @param friendListener ResourceListener the list of Friends is returned to
     */
    private static void getFacebookFriends(final UserMetadata user,
                                           final ResourceListener<Friend> friendListener) {
        // create Facebook graph request callback
        GraphRequest.Callback friendsCallback = new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                handleFacebookFriendsResponse(response, friendListener);
            }
        };
        makeFacebookFriendsRequest(friendsCallback, user.getFacebookId());
    }

    /**
     * Parses a Facebook graph response from requesting a list of friends into a list of Friend
     * model objects and replaces each friend's Facebook display name with their Snaption display
     * name. Returns the list of Friends to the given ResourceListener
     *
     * @param response Facebook graph response from requesting a list of friends
     * @param friendListener ResourceListener the list of Friends is returned to
     */
    private static void handleFacebookFriendsResponse(
            GraphResponse response, final ResourceListener<Friend> friendListener) {
        // parse the Facebook response to a list of Friends
        final JSONArray friends = response.getJSONObject() != null ?
                response.getJSONObject().optJSONArray(FB_REQUEST_DATA) : null;
        if (friends != null) {
            for (int ndx = 0; ndx < friends.length(); ndx++) {
                final JSONObject friend = friends.optJSONObject(ndx);
                if (friend != null) {
                    final String friendFacebookId = friend.optString(FB_REQUEST_ID);
                    // get the friend's User info
                    getUserMetadataByFacebookId(friendFacebookId, new ResourceListener<UserMetadata>() {
                        @Override
                        public void onData(UserMetadata user) {
                            if (user != null) {
                                friendListener.onData(new Friend(user.getId(),
                                        user.getDisplayName(), user.getEmail(), friendFacebookId));
                            }
                        }

                        @Override
                        public Class getDataType() {
                            return UserMetadata.class;
                        }
                    });
                }
            }
        }
    }

}
