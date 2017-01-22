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

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by jason_000 on 1/21/2017.
 */

public class FirebaseUploadMethods implements Uploader{
    public static final String storageBucket = "gs://vertical-prototype-81b3e.appspot.com";
    private static final String usersPath = "usersSnaption";
    private static final String gamesPath = "games";
    private static final int progressDivisor = 1000;
    public static final String imagePath = "images";
    private Context context;
    private View view;

    public FirebaseUploadMethods(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    public void addGame(Game game, byte[] photo) {
        //TODO notify invited players
        uploadPhoto(game, photo);

        // Add gameId to user's gamesList
        addGameToUserTable(game);

        // Add game object to games table
        String gameId = game.getId();
        DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference(gamesPath
                + "/" + gameId);
        gamesRef.setValue(game);
    }

    private void addGameToUserTable(Game game) {
        final String gameId = game.getId();
        String userId = game.getPicker();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(usersPath + "/" + userId);
        //TODO if games stays as a List instead of map, is PITA (can't do .push()). See below
        //Also see blog https://firebase.googleblog.com/2014/04/best-practices-arrays-in-firebase.html
//        userRef.child("games").push().setValue(gameId);
        //Assuming we'll leave it as a list
        final DatabaseReference userGameListRef = userRef.child("games");
        userGameListRef.addValueEventListener(new ValueEventListener() {
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

    private void uploadPhoto(Game game, byte[] photo) {
        // Upload photo to storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageLoc = storage.getReferenceFromUrl(storageBucket)
                .child("TestImages/" + game.getId() + ".jpg");
        UploadTask uploadTask = imageLoc.putBytes(photo);
        //Creating the progress dialog
        final ProgressDialog loadingDialog = new ProgressDialog(context);
        loadingDialog.setIndeterminate(false);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingDialog.setProgress(0);
        loadingDialog.setProgressNumberFormat("%1dKB/%2dKB");
        loadingDialog.setMessage("Uploading photo");
        loadingDialog.setMax(photo.length/progressDivisor);
        //Display progress dialog
        loadingDialog.show();

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.hide();
                e.printStackTrace();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // When done uploading, hide the progress bar
                loadingDialog.hide();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //Display progress as it updates
                loadingDialog.setProgress((int) taskSnapshot.getBytesTransferred()/progressDivisor);
            }
        });
    }

    @Override
    public void addCaptions(Caption caption) {

    }

    @Override
    public void addUser(User user, byte[] photo) {

    }

    @Override
    public void addUpvote(String captionId, String upvoterId, String captionerId, String gameId) {

    }
}
