package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by austinrobarts on 4/11/17.
 */
public class EventListenCreator {

    public static ValueEventListener getValueEventListener(final Class type, final ResourceListener listener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    listener.onData(dataSnapshot.getValue(type));
                } catch (Exception err) {
                    FirebaseReporter.reportException(err, "Crash trying to get data from Firebase. Type: " + type.toString());
                    listener.onData(null);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                listener.onData(null);
            }
        };
    }

    public static ValueEventListener getValueEventListener(final GenericTypeIndicator type, final ResourceListener listener) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    listener.onData(dataSnapshot.getValue(type));
                } catch (Exception err) {
                    FirebaseReporter.reportException(err, "Crash trying to get data from Firebase. Type: " + type.toString());
                    listener.onData(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                listener.onData(null);
            }
        };
    }
}
