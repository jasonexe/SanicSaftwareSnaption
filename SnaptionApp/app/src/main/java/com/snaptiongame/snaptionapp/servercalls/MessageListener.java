package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Returned by download - will give you whatever the current message is
 */

public class MessageListener {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    String message;
    boolean hasUpdated = false;

    public MessageListener(String path, String toSet) {
        DatabaseReference myRef = database.getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Getting tha data");
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                message = dataSnapshot.getValue(String.class);
                hasUpdated = true;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //Freezes
    public String getMessage() {
        //while(!hasUpdated) {
            ; //Wait until updated is true
        //}
        hasUpdated = false;
        return message;
    }
}
