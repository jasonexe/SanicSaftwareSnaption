package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

public class FirebaseResourceManager {
    private static final String GAME_IMAGE_DIRECTORY = "images/";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static StorageReference storage = FirebaseStorage.getInstance().getReference();

    public static void retrieveAllWithUpdates(String path, final ResourceListener listener) {
        DatabaseReference myRef = database.getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data;
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    data = new ArrayList<>();
                    for (DataSnapshot snapshot : snapshots) {
                        ((List) data).add(snapshot.getValue(listener.getDataType()));
                    }
                }
                else {
                    data = dataSnapshot.getValue(listener.getDataType());
                }

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
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
}
