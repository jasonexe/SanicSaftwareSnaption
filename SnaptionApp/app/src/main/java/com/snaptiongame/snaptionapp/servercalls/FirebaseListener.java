package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.MessageUpdater;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Returned by download - will give you whatever the current message is
 */

public class FirebaseListener {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    boolean hasUpdated = false;

    public FirebaseListener(String path, final MessageUpdater callOnUpdate) {
        DatabaseReference myRef = database.getReference(path);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("Getting tha data");
                Object data;
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    data = new ArrayList<>();
                    for (DataSnapshot snapshot : snapshots) {
                        ((List) data).add(snapshot.getValue(callOnUpdate.getDataType()));
                    }
                }
                else {
                    data = dataSnapshot.getValue(callOnUpdate.getDataType());
                }

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                callOnUpdate.onUpdate(data);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
