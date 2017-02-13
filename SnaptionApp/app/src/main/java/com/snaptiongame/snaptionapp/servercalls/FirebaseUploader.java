package com.snaptiongame.snaptionapp.servercalls;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.snaptiongame.snaptionapp.models.Caption;
import com.snaptiongame.snaptionapp.models.Friend;
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason_000 on 1/21/2017.
 */

public class FirebaseUploader implements Uploader {

    private static final String USERS_PATH = "users";
    private static final String CAPTION_PATH = "captions";
    private static final String GAMES_PATH = "games";
    private static final String IMAGE_PATH = "images";
    private static final String FRIENDS_PATH = "users/%s/friends";

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
        //TODO notify invited players
        game.setImagePath(IMAGE_PATH + "/" + game.getId());
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
        // Add gameId to user's gamesList
        addGameToUserTable(game);
        // Add game object to games table
        String gameId = game.getId();
        DatabaseReference gamesRef = database.getReference(GAMES_PATH + "/" + gameId);
        gamesRef.setValue(game);
    }

    @Override
    public String getNewGameKey() {
        DatabaseReference gamesFolderRef = database.getReference(GAMES_PATH);
        String key = gamesFolderRef.push().getKey();
        return key;
    }

    @Override
    public String getNewCaptionKey(String gameId) {
        DatabaseReference captionFolderRef = database.getReference(GAMES_PATH + "/" + gameId + "/" +
                CAPTION_PATH);
        return captionFolderRef.push().getKey();
    }

    @Override
    public String getNewUpvoteKey() {
        //TODO implement this
        return null;
    }

    private void addGameToUserTable(Game game) {
        final String gameId = game.getId();
        String userId = game.getPicker();
        DatabaseReference userRef = database.getReference(USERS_PATH + "/" + userId);
        //TODO if games stays as a List instead of map, is PITA (can't do .push()). See below
        //Also see blog https://firebase.googleblog.com/2014/04/best-practices-arrays-in-firebase.html
//        userRef.child("games").push().setValue(gameId);
        //Assuming we'll leave it as a list
        final DatabaseReference userGameListRef = userRef.child("games");
        userGameListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> userGames = dataSnapshot.getValue(t);
                if(userGames == null) {
                    //create a new list if there isn't one in Firebase yet (user's first game!)
                    userGames = new ArrayList<String>();
                }
                userGames.add(gameId);
                userGameListRef.setValue(userGames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Adding game error");
            }
        });
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
        String gameCaptionPath = GAMES_PATH + "/" + gameId + "/" + CAPTION_PATH + "/" + captId;
        uploadObject(gameCaptionPath, caption);
        String userCaptionPath = USERS_PATH + "/" + userId + "/" + CAPTION_PATH + "/" + captId;
        uploadObject(userCaptionPath, caption);
    }

    @Override
    public void addUser(final User user, final byte[] photo, final ResourceListener<User> listener) {
        //check if User already exists in Database
        FirebaseResourceManager manager = new FirebaseResourceManager();
        manager.retrieveSingleNoUpdates(USERS_PATH + "/" + user.getId(), new ResourceListener() {
            @Override
            public void onData(User data) {
                //if User does not exist
                if (data == null || !(data instanceof User)) {
                    //upload user
                    uploadObject(USERS_PATH + "/" + user.getId(), user);
                    //upload user photo
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getImagePath());
                    ref.putBytes(photo);
                    //upload user
                    uploadObject(usersPath + "/" + user.getId(), user);


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

    @Override
    public void addUpvote(String captionId, String upvoterId, String captionerId, String gameId) {

    }

    public void addFriend(final User user, final Friend friend, final UploadListener listener) {
        // TODO check if already friends before adding
        // add the friend to the user's friends list
        addFriendToList(user.getId(), friend.snaptionId, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // add the user to the friend's friends list
                    addFriendToList(friend.snaptionId, user.getId(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                listener.onComplete();
                            }
                            else {
                                // this is a problem if one friend was added but the other one was not
                                listener.onError();
                            }
                        }
                    });
                }
                else {
                    listener.onError();
                }
            }
        });
    }

    /**
     * Adds the friendId to the friend list of User associated with the userId
     *
     * @param userId
     * @param friendId
     * @param listener
     */
    private void addFriendToList(String userId, final String friendId, final DatabaseReference.CompletionListener listener) {
        final DatabaseReference userFriendsRef = database.getReference().child(String.format(FRIENDS_PATH, userId));
        userFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> userFriends = dataSnapshot.getValue(t);
                if(userFriends == null) {
                    //create a new list if there isn't one in Firebase yet (user's first friend!)
                    userFriends = new ArrayList<String>();
                }
                userFriends.add(friendId);
                userFriendsRef.setValue(userFriends, listener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onComplete(databaseError, null);
            }
        });
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
        DatabaseReference friendRef = userFriendsRef.push();
        friendRef.setValue(friendId, listener);
    }
}
