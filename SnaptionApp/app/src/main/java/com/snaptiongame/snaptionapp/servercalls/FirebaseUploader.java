package com.snaptiongame.snaptionapp.servercalls;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.snaptiongame.snaptionapp.Constants.GAME_CAPTIONS_PATH;
import static com.snaptiongame.snaptionapp.Constants.GAME_CAPTION_PATH;
import static com.snaptiongame.snaptionapp.Constants.GAME_PATH;
import static com.snaptiongame.snaptionapp.Constants.USER_CAPTION_PATH;
import static com.snaptiongame.snaptionapp.Constants.USER_CREATED_GAME_PATH;
import static com.snaptiongame.snaptionapp.Constants.USER_NOTIFICATION_PATH;
import static com.snaptiongame.snaptionapp.Constants.USER_PATH;

/**
 * FirebaseUploader is used for uploading data to Firebase and updating values.
 *
 * @author Jason Krein, Cameron Geehr
 */

public class FirebaseUploader implements Uploader {

    public static final String GAME_PLAYERS_PATH = "games/%s/players";
    private static final String USERS_PATH = "users";
    private static final String USERS_CREATED_GAMES =  "createdGames";
    private static final String CAPTION_PATH = "captions";
    private static final String GAMES_PATH = "games";
    private static final String IMAGE_PATH = "images";
    private static final String FRIENDS_PATH = "users/%s/friends";
    private static final String NOTIFICATION_ID_PATH = "users/%s/notificationId";
    private static final String USER_CAPTIONS_UPVOTES_PATH = "users/%s/captions/%s/votes";
    private static final String GAME_CAPTIONS_UPVOTES_PATH = "games/%s/captions/%s/votes";
    private static final String USER_PRIVATE_GAMES_PATH = "users/%s/privateGames/%s";
    private static final String FIREBASE_SERVER_KEY = "AAAA1YbN64o:APA91bFkAACOweZYo_FRyN6lIVKEvAoNstDavdLgXPjm4c74WN71kmCQjfR0m6bVaktnejgbbuaAyZp-vWclxv6-sZjm8iW9oyfqTep4fsuA5gZAfPYXJxI5vmkNd5Zzb3d2-p6nchpkcM-go2DfwSXn-BFF9fKTFg\n";
    private static final String FIREBASE_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String POST = "POST";
    private static final String JSON_TO = "to";
    private static final String JSON_DATA = "data";
    private static final String JSON_NOTIFICATION = "notification";
    private static final String JSON_TITLE = "title";
    private static final String JSON_BODY = "body";
    private static final String JSON_PRIORITY_KEY = "priority";
    private static final String JSON_PRIORITY_VAL = "high";
    private static final String JSON_BADGE_KEY = "badge";
    private static final String JSON_BADGE_VAL = "enabled";
    private static final String JSON_AUTH = "Authorization";
    private static final String JSON_AUTH_KEY = "key=";
    private static final String JSON_CONTENT_TYPE = "Content-Type";
    private static final String JSON_CONTENT_VAL = "application/json";
    private static final String USERNAME_PATH = "users/%s/displayName";
    private static final String LOWERCASE_USERNAME_PATH = "users/%s/lowercaseDisplayName";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static void uploadObject(String firebasePath, Object content) {
        DatabaseReference myRef = database.getReference(firebasePath);
        myRef.setValue(content);
    }

    public static void deleteValue(String firebasePath) {
        DatabaseReference myRef = database.getReference(firebasePath);
        myRef.removeValue();
    }


    public FirebaseUploader() {

    }

    public interface UploadDialogInterface {

        public void onStartUpload(long maxBytes);

        public void onUploadProgress(long bytes);

        public void onUploadDone();
    }

    /**
     * Call this if the game needs to be uploaded
     * @param game The game to upload. Its image path should be just the name of the file.
     * @param photo Byte array of photo
     * @param uploadCallback interface to call that activates the upload dialog
     */
    @Override
    public void addGame(Game game, byte[] photo, UploadDialogInterface uploadCallback) {
        game.setImagePath(String.format(Constants.STORAGE_IMAGE_PATH, game.getId()));
        uploadPhoto(game, photo, uploadCallback);
        addCompletedGameObj(game);
    }

    /**
     * Call this if you're adding a game with an existing photo. AKA create game from existing
     * @param game Game object with fields filled in
     */
    @Override
    public void addGame(Game game) {
        // AddNewGame activity calls this if game already exists,
        // so image path should be fine already, since it was pulled from the existing game
        addCompletedGameObj(game);
    }

    private void addCompletedGameObj(Game game) {
        // Add game object to games table
        String gameId = game.getId();
        DatabaseReference gamesRef = database.getReference(String.format(GAME_PATH, gameId));
        gamesRef.setValue(game);
        // Add gameId to user's createdGames map
        addGameToUserCreatedGames(game);
        // Add gameId to all players' privateGames map
        addGameToPlayerPrivateGames(game);
        //notify players if there are any
        if (game.getPlayers() != null) {
            notifyPlayersGameCreated(game.getId(), game.getPlayers().keySet());
        }
    }

    private void notifyPlayersGameCreated(final String gameId, final Set<String> players) {

        final String pickerId = FirebaseResourceManager.getUserId();
        //listener once you get a user to send notification
        final ResourceListener<User> notifyPlayerListener = new ResourceListener<User>() {
            @Override
            public void onData(User receiver) {
                if (receiver != null) {
                    FirebaseNotificationSender.sendGameCreationNotification(receiver, gameId, pickerId);
                }
            }
            @Override
            public Class getDataType() {
                return User.class;
            }
        };

        //for each player invited to the game, send notification
        for (String playerId : players) {
            //dont send notificaiton to picker
            if (!playerId.equals(pickerId)) {
                FirebaseResourceManager.retrieveSingleNoUpdates(USERS_PATH + "/" + playerId,
                        notifyPlayerListener);
            }
        }
    }

    @Override
    public String getNewGameKey() {
        DatabaseReference gamesFolderRef = database.getReference(GAMES_PATH);
        return gamesFolderRef.push().getKey();
    }

    @Override
    public String getNewCaptionKey(String gameId) {
        DatabaseReference captionFolderRef = database.getReference(String.format(GAME_CAPTIONS_PATH, gameId));
        return captionFolderRef.push().getKey();
    }

    private void addGameToUserCreatedGames(Game game) {
        final String gameId = game.getId();
        String userId = game.getPicker();
        DatabaseReference userRef = database.getReference(String.format(USER_CREATED_GAME_PATH, userId, gameId));
        //Also see blog https://firebase.googleblog.com/2014/04/best-practices-arrays-in-firebase.html
        userRef.setValue(1);
    }

    private void addGameToPlayerPrivateGames(Game game) {
        String gameId = game.getId();
        Set<String> ids = game.getPlayers().keySet();
        // add the game to the picker's private games map
        database.getReference(String.format(Constants.USER_PRIVATE_GAMES_PATH, game.getPicker(), gameId))
                .setValue(1);
        // add the game to each of the player's private games map
        for (String id : ids) {
            database.getReference(String.format(Constants.USER_PRIVATE_GAMES_PATH, id, gameId)).setValue(1);
        }
    }

    private void uploadPhoto(Game game, byte[] photo, final UploadDialogInterface uploadCallback) {
        // Upload photo to storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageLoc = storage.getReference()
                .child(game.getImagePath());
        UploadTask uploadTask = imageLoc.putBytes(photo);
        //Creating the progress dialog
        uploadCallback.onStartUpload(photo.length);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadCallback.onUploadDone();
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // When done uploading, hide the progress bar
                uploadCallback.onUploadDone();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                uploadCallback.onUploadProgress(taskSnapshot.getBytesTransferred());
                //Display progress as it updates
            }
        });
    }

    /**
     * This adds the caption to the database. The Caption needs to be added to both
     * the game, and to the user "Captions" map, which maps from caption Ids to captions
     * @param caption The Caption object, has all the necessary stuff
     */
    @Override
    public void addCaptions(Caption caption) {
        String gameId = caption.getGameId();
        String userId = caption.getUserId();
        String captId = caption.getId();
        String gameCaptionPath = String.format(GAME_CAPTION_PATH, gameId, captId);
        uploadObject(gameCaptionPath, caption);
        String userCaptionPath = String.format(USER_CAPTION_PATH, userId, captId);
        uploadObject(userCaptionPath, caption);
    }

    @Override
    public void addUser(final User user, final byte[] photo, final ResourceListener<User> listener) {
        //check if User already exists in Database
        FirebaseResourceManager.retrieveSingleNoUpdates(String.format(USER_PATH, user.getId()), new ResourceListener<User>() {
            @Override
            public void onData(User data) {
                //if User does not exist
                if (data == null) {
                    //upload user
                    uploadObject(String.format(USER_PATH, user.getId()), user);
                    //upload user photo
                    uploadUserPhoto(user, photo);
                } else {
                    //update notificationId every login
                    uploadObject(String.format(Constants.USER_NOTIFICATION_PATH, user.getId()), user.getNotificationId());
                    //now the user is logged in on firebase
                    uploadObject(String.format(Constants.USER_IS_ANDROID_PATH, user.getId()), user.getIsAndroid());
                }
                //notify user has been added or found
                listener.onData(data);
            }

            @Override
            public Class getDataType() {
                return User.class;
            }
        });
    }

    public static void uploadUserPhoto(User user, byte[] photo) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getImagePath());
        ref.putBytes(photo);
    }

    public static void updateUserNotificationToken(String userId, final String token) {
        uploadObject(String.format(USER_NOTIFICATION_PATH, userId), token);
    }

    /**
     * Adds the upvote to the caption in the user object and in the game object.
     *
     * @param captionId The ID of the caption
     * @param upvoterId The ID of the player upvoting the caption
     * @param captionerId The ID of the player who created the caption
     * @param gameId The ID of the game that the caption is in
     */
    public static void addUpvote(final String captionId, final String upvoterId,
                                 final String captionerId, String gameId,
                                 final UploadListener listener) {
        updateUpvote(captionId, upvoterId, captionerId, gameId, listener, true);
    }

    /**
     * Removes the upvote from the caption in the user object and in the game object.
     *
     * @param captionId The ID of the caption
     * @param upvoterId The ID of the player un-upvoting the caption
     * @param captionerId The ID of the player who created the caption
     * @param gameId The ID of the game that the caption is in
     */
    public static void removeUpvote(final String captionId, final String upvoterId,
                                    final String captionerId, String gameId,
                                    final UploadListener listener) {
        updateUpvote(captionId, upvoterId, captionerId, gameId, listener, false);
    }

    /**
     * Updates the value for the upvote. If isAdd is true, it will be added with a value of 1, if
     * isAdd is false, it will be removed by setting its value to null.
     *
     * @param captionId
     * @param upvoterId
     * @param captionerId
     * @param gameId
     * @param listener
     * @param isAdd
     */
    private static void updateUpvote(final String captionId, final String upvoterId,
                                     final String captionerId, String gameId,
                                     final UploadListener listener, boolean isAdd) {
        // Checks if the upvote is being added or removed
        Integer value = isAdd ? 1 : null;

        Map<String, Object> childUpdates = new HashMap<>();
        // Updates for the game caption
        childUpdates.put(String.format(Constants.GAME_CAPTIONS_UPVOTER_PATH, gameId, captionId, upvoterId), value);
        // Updates for the user caption
        childUpdates.put(String.format(Constants.USER_CAPTIONS_UPVOTE_PATH, captionerId, captionId, upvoterId), value);
        // Updates the values in the database
        database.getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                if (databaseError == null) {
                    listener.onComplete();
                }
                else {
                    listener.onError(databaseError.getMessage().contains(ITEM_ALREADY_EXISTS_ERROR)
                            ? ITEM_ALREADY_EXISTS_ERROR : "");
                };
            }
        });
    }

    public static void addCurrentUserToGame(Game game, final ResourceListener<Exception> errorDisplayer) {
        // TODO check that joined games should actually go in private in the user
        Map<String, Object> childUpdates = new HashMap<>();
        String gameId = game.getId();
        String userId = FirebaseResourceManager.getUserId();

        childUpdates.put(String.format(Constants.GAME_PLAYERS_PATH, gameId) + "/" + userId, 1);
        childUpdates.put(String.format(Constants.USER_PRIVATE_GAMES_PATH, userId, gameId), 1);
        database.getReference().updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorDisplayer.onData(e);
            }
        });
    }

    public void addFriend(final User user, final Friend friend, final UploadListener listener) {
        // add the friend to the user's friends list
        addFriendToHashMap(user.getId(), friend.snaptionId, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // add the user to the friend's friends list
                    addFriendToHashMap(friend.snaptionId, user.getId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                listener.onComplete();
                            }
                            else {
                                // this is a problem if one friend was added but the other one was not
                                listener.onError(getErrorMessage(databaseError));
                            }
                        }
                    });
                }
                else {
                    listener.onError(getErrorMessage(databaseError));
                }
            }
        });
    }

    public static void updateDisplayName(String newName, String userId) {
        uploadObject(String.format(USERNAME_PATH, userId), newName);
        uploadObject(String.format(LOWERCASE_USERNAME_PATH, userId), newName.toLowerCase());
    }

    /**
     * Determines the proper Uploader error message to display depending on the DatabaseError
     * received
     * @param databaseError received DatabaseError
     * @return Uploader error message
     */
    private String getErrorMessage(DatabaseError databaseError) {
        String errorMessage = "";
        if (databaseError.getMessage().contains(ITEM_ALREADY_EXISTS_ERROR)) {
            errorMessage = ITEM_ALREADY_EXISTS_ERROR;
        }
        return errorMessage;
    }

    /**
     * Adds the friendId to the friend hash map of User associated with the userId
     *
     * @param userId
     * @param friendId
     * @param listener
     */
    private void addFriendToHashMap(String userId, String friendId, DatabaseReference.CompletionListener listener) {
        DatabaseReference userFriendsRef = database.getReference().child(String.format(FRIENDS_PATH, userId));
        DatabaseReference friendRef = userFriendsRef.child(friendId);
        friendRef.setValue(1, listener);
    }

}