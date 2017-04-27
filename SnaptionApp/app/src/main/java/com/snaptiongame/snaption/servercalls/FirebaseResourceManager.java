package com.snaptiongame.snaption.servercalls;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Card;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.android.gms.internal.zzs.TAG;

public class FirebaseResourceManager {
    private static final String SMALL_FB_PHOTO_REQUEST = "https://graph.facebook.com/%s/picture?type=small";
    private static final String FB_FRIENDS_REQUEST = "/%s/friends";
    private static final String FB_REQUEST_DATA = "data";
    private static final String FB_REQUEST_ID = "id";
    private static final String FB_ID_CHILD = "facebookId";
    protected static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();
    private static FirebaseImageLoader imageLoader = new FirebaseImageLoader();

    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;

    public FirebaseResourceManager() {}

    /**
     * Notifies the given ResourceListener of when elements in the table of the given path is
     * changed.
     *
     * @param path The table path name
     * @param listener A ResourceListener for a Map of the resource class type associated with the
     *                 table elements
     */
    public void retrieveMapWithUpdates(String path, final ResourceListener listener) {
        // if the FirebaseResourceManager is already being used to listen to the db, remove the
        // previous listener
        removeListener();

        databaseReference = database.getReference(path);
        GenericTypeIndicator<Map<String, Object>> genericTypeIndicator =
                new GenericTypeIndicator<Map<String, Object>>() {};
        valueEventListener = EventListenCreator.getValueEventListener(genericTypeIndicator, listener);
        databaseReference.addValueEventListener(valueEventListener);
    }

    public void addChildListener(String path, final ChildResourceListener listener) {
        removeListener();
        databaseReference = database.getReference(path);
        ChildEventListener childListener = EventListenCreator.getChildEventListener(listener.getDataType(), listener);
        databaseReference.addChildEventListener(childListener);
    }

    /**
     * Gets the direct path to the user table in the database
     * @return a string path from the root node to current user
     */
    /*public static String getUserPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userPath = null;
        if (user != null)
            userPath = String.format(Constants.USER_PATH, user.getUid());
        return userPath;
    }*/

    /**
     * Gets the direct path to the user table in the database
     * @param id The ID of the user whose path to find
     * @return a string path from the root node to current user
     */
    /*public static String getUserPath(String id) {
        return String.format(Constants.USER_PATH, id);
    }*/

    /**
     * Get the user id of the current user
     * @return a string key to the user table
     */
    /*public static String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = null;
        if (user != null) {
            id = user.getUid();
        }
        return id;
    }*/

    /**
     * Gets the list of providers that the current user's account is associated with.
     * @return the list of providers the user's account is associated with
     */
    /*public static List<String> getProviders() {
        List<String> providers = new ArrayList<String>();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            providers = currentUser.getProviders();
        }
        return providers;
    }*/

    /**
     * Set up a listener to receive an object at a specified path without a connection
     * for future data changes
     * @param path the path to the object requested from Firebase
     * @param listener this will be waiting to receive the object requested
     */
    public static void retrieveSingleNoUpdates(String path, final ResourceListener listener) {
        //used for just receiving data once
        DatabaseReference ref = database.getReference(path);
        ref.addListenerForSingleValueEvent(EventListenCreator.getValueEventListener(listener.getDataType(), listener));
    }

    /**
     * Set up a listener to receive a map of strings at a specified path without a connection
     * for future data changes
     *
     * @param path the path to the list of strings requested from Firebase
     * @param listener this will be waiting to receive the object requested
     */
    public static void retrieveStringMapNoUpdates(String path,
                                                  final ResourceListener<Map<String, Integer>> listener) {
        final DatabaseReference ref = database.getReference().child(path);
        GenericTypeIndicator<Map<String, Integer>> type = new GenericTypeIndicator<Map<String, Integer>>() {};
        ref.addListenerForSingleValueEvent(EventListenCreator.getValueEventListener(type, listener));
    }

    /**
     * Notifies the given ResourceListener of when a single element of the given path is changed
     *
     * @param path The single element path name
     * @param listener A ResourceListener for the resource class type associated with the single
     *                 element
     */
    public void retrieveSingleWithUpdates(String path, final ResourceListener listener) {
        // if the FirebaseResourceManager is already being used to listen to the db, remove the
        // previous listener
        removeListener();

        databaseReference = database.getReference(path);
        valueEventListener = EventListenCreator.getValueEventListener(listener.getDataType(), listener);
        databaseReference.addValueEventListener(valueEventListener);
    }

    /**
     * Stops db notifications to a previously set ResourceListener
     */
    public void removeListener() {
        if (databaseReference != null && valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
            databaseReference = null;
            valueEventListener = null;
        }
    }

    /**
     * Loads a random five cards from the given pack name.
     * @param packName Name of the pack
     * @param listener Listener to call with the arraylist of strings containing the cards
     */
    public static void loadCardsFromPack(String packName,
                                         final ResourceListener<List<Card>> listener) {
        //Gets locale. Cards is either cards_en or cards_es. Where should we validate this?
        String directory = String.format(Constants.CARDS_DIRECTORY, Locale.getDefault().getLanguage(), packName);
        DatabaseReference cardsRef = database.getReference(directory);
        GenericTypeIndicator<List<Card>> typeIndicator = new GenericTypeIndicator<List<Card>>() {};
        cardsRef.addListenerForSingleValueEvent(EventListenCreator.getValueEventListener(typeIndicator, listener));
    }

    /**
     * Loads an image from Firebase into a given ImageView and notifies the given listener when the
     * inmage has been loaded into the ImageView.
     *
     * @param imagePath Image file path name
     * @param imageView ImageView in which the image should be loaded
     * @param listener ResourceListener to be notified when the image has been loaded
     */
    public static void loadImageIntoView(final String imagePath, final ImageView imageView,
                                         final ResourceListener<Bitmap> listener) {
        StorageReference ref = storage.child(imagePath);
        try {
            Glide.with(imageView.getContext())
                    .using(imageLoader)
                    .load(ref).fitCenter()
                    .dontAnimate()
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            if (listener != null) {
                                listener.onData(null);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource,
                                                       StorageReference model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            if (listener != null) {
                                listener.onData(((GlideBitmapDrawable) resource).getBitmap());
                            }
                            return false;
                        }
                    })
                    .into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            FirebaseReporter.reportException(e, "Glide image load failed");
        }
    }

    /**
     * Loads an image from Firebase into a given ImageView.
     *
     * @param imagePath The image file path name
     * @param imageView The ImageView in which the image should be loaded
     */
    public static void loadImageIntoView(String imagePath, final ImageView imageView) {
        StorageReference ref = storage.child(imagePath);
        try {
            Glide.with(imageView.getContext())
                    .using(imageLoader)
                    .load(ref)
                    .fitCenter()
                    .dontAnimate()
                    // Caches the full image to make reloading faster
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .priority(Priority.IMMEDIATE)
                    // Update signature every hour for profile picture changes.
                    .signature(new StringSignature(Long.toString(System.currentTimeMillis() / (60 * 60 * 1000))))
                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, StorageReference model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource,
                                                       StorageReference model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(android.R.drawable.progress_horizontal).into(imageView);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            FirebaseReporter.reportException(e, "Glide image load failed");
        }
    }

    public static void getImageURI(String imagePath, final ResourceListener<Uri> pathListener) {
        storage.child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                pathListener.onData(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.v("URI Error:", "Something went wrong when trying to get " +
                        "image URL from Firebase");
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
    /*private static void getFacebookFriends(final User user,
                                           final ResourceListener<Friend> friendListener) {
        // create Facebook graph request callback
        GraphRequest.Callback friendsCallback = new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                handleFacebookFriendsResponse(response, friendListener);
            }
        };
        makeFacebookFriendsRequest(friendsCallback, user.getFacebookId());
    }*/

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
    /*public static void getFacebookFriends(final User user, final Map<String, Integer> friendsFilter,
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
    }*/

    /**
     * Parses a Facebook graph response from requesting a list of friends into a list of Friend
     * model objects and replaces each friend's Facebook display name with their Snaption display
     * name. Returns the list of Friends to the given ResourceListener
     *
     * @param response Facebook graph response from requesting a list of friends
     * @param friendListener ResourceListener the list of Friends is returned to
     */
     /*private static void handleFacebookFriendsResponse(
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
                    getFacebookUser(friendFacebookId, new ResourceListener<User>() {
                        @Override
                        public void onData(User user) {
                            if (user != null) {
                                friendListener.onData(new Friend(user.getId(),
                                        user.getDisplayName(), user.getEmail(), friendFacebookId));
                            }
                        }

                        @Override
                        public Class getDataType() {
                            return String.class;
                        }
                    });
                }
            }
        }
    }*/

    /**
     * Retrieves the Snaption User associated with the given Facebook unique id, and returns the
     * User to the given ResourceListener
     *
     * @param facebookId String unique Facebook id of a Facebook user
     * @param resourceListener ResourceListener the user is returned to
     */
    /*public static void getFacebookUser(String facebookId, final ResourceListener<User> resourceListener) {
        Query query = database.getReference(String.format(Constants.USERS_PATH)).orderByChild(FB_ID_CHILD)
                .equalTo(facebookId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> facebookUserIterator = dataSnapshot.getChildren().iterator();
                if (facebookUserIterator.hasNext()) {
                    User user = facebookUserIterator.next().getValue(User.class);
                    resourceListener.onData(user);
                }
                else {
                    resourceListener.onData(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                resourceListener.onData(null);
            }
        });
    }*/

    /**
     * Creates and executes a Facebook graph request to retrieve the Facebook friends of the
     * Facebook user with the given facebookId and returns the results to the given friendsCallback
     *
     * @param friendsCallback GraphRequest.Callback the results are returned to
     * @param facebookId String unique Facebook id of a Facebook user
     */
    public static void makeFacebookFriendsRequest(GraphRequest.Callback friendsCallback,
                                                  String facebookId) {
        new GraphRequest(AccessToken.getCurrentAccessToken(), String.format(FB_FRIENDS_REQUEST,
                facebookId), null, HttpMethod.GET, friendsCallback).executeAsync();
    }


    /**
     * Loads a small Facebook photo into an ImageView given a Facebook user's unique Facebook id
     *
     * @param facebookId Facebook user's unique Facebook id
     * @param imageView ImageView in which the photo should be loaded
     */
    public static void loadSmallFbPhotoIntoImageView(String facebookId, ImageView imageView) {
        Glide.with(imageView.getContext()).load(String.format(SMALL_FB_PHOTO_REQUEST,
                facebookId)).into(imageView);
    }

    /**
     * Checks if a String is valid for a Firebase path by making sure it does not contain
     * any of the following characters: '.', '#', '$', '[', or ']'
     *
     * @param path The path to be checked
     * @return True if the path does not contain any of the characters, false otherwise.
     */
    public static boolean validFirebasePath(String path) {
        Pattern pattern = Pattern.compile("[.#$\\[\\]]");
        return !pattern.matcher(path).find();
    }
}
