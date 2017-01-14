package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.MessageUpdater;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Returned by download - will give you whatever the current message is
 */

public class FirebaseListener {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    String message;
    boolean hasUpdated = false;

    public FirebaseListener(String path, final MessageUpdater callOnUpdate) {
        DatabaseReference myRef = database.getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Getting tha data");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                message = dataSnapshot.getValue(String.class);
                callOnUpdate.onUpdate(message);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
