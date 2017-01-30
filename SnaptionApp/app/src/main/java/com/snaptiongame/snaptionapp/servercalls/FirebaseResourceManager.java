package com.snaptiongame.snaptionapp.servercalls;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseUploader.imagePath;

public class FirebaseResourceManager {
    private static final String GAME_IMAGE_DIRECTORY = "images/";
    private static final String PROFILE_PIC_DIRECTORY = "ProfilePictures/";
    private static final String USER_DIRECTORY = "users/";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();

    private ValueEventListener valueEventListener;
    private DatabaseReference databaseReference;

    public FirebaseResourceManager() {}

    /**
     * Notifies the given ResourceListener of when elements in the table of the given path is changed
     *
     * @param path The table path name
     * @param listener A ResourceListener for a List of the resource class type associated with the
     *                 table elements
     */
    public void retrieveAllWithUpdates(String path, final ResourceListener listener) {
        // if the FirebaseResourceManager is already being used to listen to the db, remove the
        // previous listener
        removeListener();

        databaseReference = database.getReference(path);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : snapshots) {
                    data.add(snapshot.getValue(listener.getDataType()));
                }
                // Notify the ResourceListener that data was received
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);
    }

    /**
     * Gets the direct path to the user table in the database
     * @return a string path from the root node to current user
     */
    public static String getUserPath() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userPath = null;
        if (user != null)
            userPath = USER_DIRECTORY + user.getUid();
        return userPath;
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
     * Set up a listener to receive an object at a specified path without a connection
     * for future data changes
     * @param path the path to the object requested from Firebase
     * @param listener this will be waiting to receive the object requested
     */
    public void retrieveSingleNoUpdates(String path, final ResourceListener listener) {
        //used for just receiving data once
        DatabaseReference ref = database.getReference(path);
        ValueEventListener firebaseResponse = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue(listener.getDataType());
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        //set up single event listener that only sends info once
        ref.addListenerForSingleValueEvent(firebaseResponse);

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
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue(listener.getDataType());
                // Notify the ResourceListener that data was received
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        };
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
     * Loads an image from the game root directory from Firebase into a given ImageView
     *
     * @param imagePath The image file path name
     * @param imageView The ImageView in which the image should be loaded
     */
    public static void loadGameImageIntoView(String imagePath, ImageView imageView) {
        loadImageIntoView(GAME_IMAGE_DIRECTORY, imagePath, imageView);
    }

    /**
     * Loads an image from the game root directory from Firebase into a given ImageView
     *
     * @param imagePath The image file path name
     * @param imageView The ImageView in which the image should be loaded
     */
    public static void loadProfilePictureIntoView(String imagePath, ImageView imageView) {
        loadImageIntoView("", imagePath, imageView);
    }

    /**
     * Loads an image from Firebase into a given ImageView.
     *
     * @param imageDirectory The root directory of the image
     * @param imagePath The image file path name
     * @param imageView The ImageView in which the image should be loaded
     */
    private static void loadImageIntoView(String imageDirectory, String imagePath, final ImageView imageView) {
        StorageReference ref = storage.child(imageDirectory + imagePath);
        Glide.with(imageView.getContext())
                .using(new FirebaseImageLoader())
                .load(ref).fitCenter()
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
                        imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        return false;
                    }
                })
                .placeholder(android.R.drawable.progress_horizontal).into(imageView);
    }

    public static void getImageURI(String imagePath, final ResourceListener<Uri> pathListener) {
        storage.child(GAME_IMAGE_DIRECTORY + "/" + imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
}
