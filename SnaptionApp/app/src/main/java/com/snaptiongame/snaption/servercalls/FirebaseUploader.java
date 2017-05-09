package com.snaptiongame.snaption.servercalls;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snaptiongame.snaption.models.Caption;
import com.snaptiongame.snaption.models.Friend;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.GameData;
import com.snaptiongame.snaption.models.GameMetadata;
import com.snaptiongame.snaption.models.UserMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.snaptiongame.snaption.Constants.ASPECT_RATIO_KEY;
import static com.snaptiongame.snaption.Constants.GAMES_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.GAME_DATA_CAPTIONS_PATH;
import static com.snaptiongame.snaption.Constants.GAME_DATA_CAPTION_PATH;
import static com.snaptiongame.snaption.Constants.GAME_DATA_PLAYERS_PATH;
import static com.snaptiongame.snaption.Constants.GAME_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PRIVATE_DATA_PLAYER_PATH;
import static com.snaptiongame.snaption.Constants.GAME_PUBLIC_DATA_PLAYER_PATH;
import static com.snaptiongame.snaption.Constants.PRIVATE;
import static com.snaptiongame.snaption.Constants.PUBLIC;
import static com.snaptiongame.snaption.Constants.USER_DISPLAY_NAME_PATH;
import static com.snaptiongame.snaption.Constants.USER_FRIENDS_PATH;
import static com.snaptiongame.snaption.Constants.USER_IS_ANDROID_PATH;
import static com.snaptiongame.snaption.Constants.USER_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.USER_NOTIFICATION_PATH;
import static com.snaptiongame.snaption.Constants.USER_SEARCH_NAME_PATH;

/**
 * FirebaseUploader is used for uploading data to Firebase and updating values.
 *
 * @author Jason Krein, Cameron Geehr
 */

public class FirebaseUploader implements Uploader {

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
     * @param data Compressed byte array of the photo
     * @param aspectRatio The aspect ratio of the photo
     * @param uploadCallback interface to call that activates the upload dialog
     */
    @Override
    public void addGame(Game game, byte[] data, double aspectRatio, UploadDialogInterface uploadCallback) {
        uploadPhoto(game, data, aspectRatio, uploadCallback);
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
        String access = game.getIsPublic() ? PUBLIC : PRIVATE;
        GameMetadata gameMetadata = game.getMetaData();
        GameData gameData = game.getData();

        //Upload the game's metadata
        DatabaseReference metaDataRef = database.getReference(
                String.format(GAME_METADATA_PATH, access, gameId));
        metaDataRef.setValue(gameMetadata);
        //Upload the game's data
        DatabaseReference dataRef = database.getReference(
                String.format(GAME_DATA_PLAYERS_PATH, access, gameId));
        dataRef.setValue(gameData.getPlayers());
        //notify players if there are any
        //TODO: once we verify firebase functions does notifications well we can remove this and FirebaseNotificationSender
        /*if (game.getPlayers() != null) {
            notifyPlayersGameCreated(game.getId(), game.getPlayers().keySet());
        }*/
    }

    private void notifyPlayersGameCreated(final String gameId, final Set<String> players,
                                          final String access) {

        final String pickerId = FirebaseUserResourceManager.getUserId();
        //listener once you get a user to send notification
        final ResourceListener<UserMetadata> notifyPlayerListener = new ResourceListener<UserMetadata>() {
            @Override
            public void onData(UserMetadata receiver) {
                if (receiver != null) {
                    FirebaseNotificationSender.sendGameCreationNotification(receiver, pickerId, gameId, access);
                }
            }
            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        };

        //for each player invited to the game, send notification
        for (String playerId : players) {
            //dont send notificaiton to picker
            if (!playerId.equals(pickerId)) {
                FirebaseUserResourceManager.getUserMetadataById(playerId, notifyPlayerListener);
            }
        }
    }

    @Override
    public String getNewGameKey(boolean isPublic) {
        String access = isPublic ? PUBLIC : PRIVATE;
        String gamesFolderPath = String.format(GAMES_METADATA_PATH, access);
        DatabaseReference gamesFolderRef = database.getReference(gamesFolderPath);
        return gamesFolderRef.push().getKey();
    }

    @Override
    public String getNewCaptionKey(Game game) {
        String access = game.getIsPublic() ? PUBLIC : PRIVATE;
        String captionsFolderPath = String.format(GAME_DATA_CAPTIONS_PATH, access, game.getId());
        DatabaseReference captionFolderRef = database.getReference(captionsFolderPath);
        return captionFolderRef.push().getKey();
    }

    private void uploadPhoto(Game game, byte[] data, double aspectRatio, final UploadDialogInterface uploadCallback) {

        StorageMetadata imageMetadata = new StorageMetadata.Builder()
                .setCustomMetadata(ASPECT_RATIO_KEY, Double.toString(aspectRatio))
                .build();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageLoc = storage.getReference()
                .child(game.getImagePath());

        UploadTask uploadTask = imageLoc.putBytes(data, imageMetadata);
        //Creating the progress dialog


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
                uploadCallback.onStartUpload(taskSnapshot.getTotalByteCount());
                //Display progress as it updates
            }
        });
    }

    /**
     * This adds the caption to the database. The Caption needs to be added to both
     * the game, and to the user "Captions" map, which maps from caption Ids to captions
     * @param caption The Caption object, has all the necessary stuff
     * @param isPublic Whether the caption is being added to a public or private game
     */
    @Override
    public void addCaptions(Caption caption, boolean isPublic) {
        String gameId = caption.getGameId();
        String userId = caption.getUserId();
        String captId = caption.getId();
        String access = isPublic ? PUBLIC : PRIVATE;

        String gameCaptionPath = String.format(GAME_DATA_CAPTION_PATH, access, gameId, captId);
        uploadObject(gameCaptionPath, caption);
    }

    @Override
    public void addUser(final UserMetadata user, final byte[] photo, final ResourceListener<UserMetadata> listener) {
        //check if User already exists in Database
        FirebaseUserResourceManager.getUserMetadataById(user.getId(), new ResourceListener<UserMetadata>() {
            @Override
            public void onData(UserMetadata data) {
                //if User does not exist
                if (data == null) {
                    //upload user
                    uploadObject(String.format(USER_METADATA_PATH, user.getId()), user);
                    //upload user photo
                    uploadUserPhoto(user.getImagePath(), photo);
                } else {
                    //update notificationId every login
                    uploadObject(String.format(USER_NOTIFICATION_PATH, user.getId()), user.getNotificationId());
                    //now the user is logged in on firebase
                    uploadObject(String.format(USER_IS_ANDROID_PATH, user.getId()), user.getIsAndroid());
                }
                //notify user has been added or found
                listener.onData(data);
            }

            @Override
            public Class getDataType() {
                return UserMetadata.class;
            }
        });
    }

    public static void uploadUserPhoto(String userImagePath, byte[] photo) {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(userImagePath);
        ref.putBytes(photo);
    }

    public static void updateUserNotificationToken(String userId, final String token) {
        uploadObject(String.format(USER_NOTIFICATION_PATH, userId), token);
    }

    /**
     * Adds the upvote to the object.
     *
     * @param path The path to the object
     * @param listener The listener to send callbacks to
     */
    public static void addUpvote(String path, final UploadListener listener) {
        updateUpvote(path, listener, true);
    }

    /**
     * Removes the upvote from the object.
     *
     * @param path The path to the object
     * @param listener The listener to send callbacks to
     */
    public static void removeUpvote(String path, final UploadListener listener) {
        updateUpvote(path, listener, false);
    }

    /**
     * Updates the value for the upvote. If isAdd is true, it will be added with a value of 1, if
     * isAdd is false, it will be removed by setting its value to null.
     *
     * @param path
     * @param listener
     * @param isAdd
     */
    private static void updateUpvote(String path, final UploadListener listener, boolean isAdd) {
        // Checks if the upvote is being added or removed
        Integer value = isAdd ? 1 : null;

        Map<String, Object> childUpdates = new HashMap<>();
        // Updates for the object
        childUpdates.put(path, value);
        // Updates the values in the database
        database.getReference().updateChildren(childUpdates,
                new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError,
                                   DatabaseReference databaseReference) {
                if (listener != null) {
                    if (databaseError == null) {
                        listener.onComplete();
                    }
                    else {
                        listener.onError(databaseError.getMessage().contains(ITEM_ALREADY_EXISTS_ERROR)
                                ? ITEM_ALREADY_EXISTS_ERROR : "");
                    }
                }
            }
        });
    }

    public static void addCurrentUserToGame(Game game, final ResourceListener<Exception> errorDisplayer) {
        // TODO check that joined games should actually go in private in the user
        Map<String, Object> childUpdates = new HashMap<>();
        String gameId = game.getId();
        String userId = FirebaseUserResourceManager.getUserId();

        String gameUserPath = game.getIsPublic() ?
                String.format(GAME_PUBLIC_DATA_PLAYER_PATH, gameId, userId) :
                String.format(GAME_PRIVATE_DATA_PLAYER_PATH, gameId, userId);
        childUpdates.put(gameUserPath, 1);

        database.getReference().updateChildren(childUpdates).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                errorDisplayer.onData(e);
            }
        });
    }

    public static void removeCurrentUserFromGame(Game game, final ResourceListener<Exception> errDisplay) {
        String gameId = game.getId();
        String userId = FirebaseUserResourceManager.getUserId();
        // If userId is null
        if(userId != null) {
            String gameUserPath = game.getIsPublic() ?
                    String.format(GAME_PUBLIC_DATA_PLAYER_PATH, gameId, userId) :
                    String.format(GAME_PRIVATE_DATA_PLAYER_PATH, gameId, userId);
            database.getReference(gameUserPath).removeValue().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    errDisplay.onData(e);
                }
            });
        } else {
            errDisplay.onData(new Exception());
        }
    }

    public void addFriend(final UserMetadata user, final Friend friend, final UploadListener listener) {
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
        uploadObject(String.format(USER_DISPLAY_NAME_PATH, userId), newName);
        uploadObject(String.format(USER_SEARCH_NAME_PATH, userId), newName.toLowerCase());
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
        DatabaseReference userFriendsRef = database.getReference().child(String.format(USER_FRIENDS_PATH, userId));
        DatabaseReference friendRef = userFriendsRef.child(friendId);
        friendRef.setValue(1, listener);
    }
}