package com.snaptiongame.snaptionapp.servercalls;

import android.app.ProgressDialog;
import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
import com.snaptiongame.snaptionapp.models.Game;
import com.snaptiongame.snaptionapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason_000 on 1/21/2017.
 */

public class FirebaseUploader implements Uploader {

    private static final String usersPath = "usersSnaption";
    private static final String captionPath = "captions";
    private static final String gamesPath = "games";
    public static final String imagePath = "images";


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

    @Override
    public void addGame(Game game, byte[] photo, UploadDialogInterface uploadCallback) {
        //TODO notify invited players
        uploadPhoto(game, photo, uploadCallback);

        // Add gameId to user's gamesList
        addGameToUserTable(game);

        // Add game object to games table
        String gameId = game.getId();
        DatabaseReference gamesRef = database.getReference(gamesPath
                + "/" + gameId);
        gamesRef.setValue(game);
    }

    @Override
    public String getNewGameKey() {
        DatabaseReference gamesFolderRef = database.getReference(gamesPath);
        String key = gamesFolderRef.push().getKey();
        return key;
    }

    @Override
    public String getNewCaptionKey(String gameId) {
        DatabaseReference captionFolderRef = database.getReference(gamesPath + "/" + gameId + "/" +
                captionPath);
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
        DatabaseReference userRef = database.getReference(usersPath + "/" + userId);
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
                .child(imagePath + "/" + game.getImagePath());
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

    @Override
    public void addCaptions(Caption caption) {

    }

    @Override
    public void addUser(User user, byte[] photo) {
        //upload user
        uploadObject(usersPath + "/" + user.getId(), user);
        //upload photo
        //String photoPath = user.getImagePath().substring(0, user.getImagePath().lastIndexOf('/') + 1);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(user.getImagePath());
        ref.putBytes(photo);

    }

    @Override
    public void addUpvote(String captionId, String upvoterId, String captionerId, String gameId) {

    }
}
