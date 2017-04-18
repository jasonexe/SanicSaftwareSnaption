package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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

    public static ChildEventListener getChildEventListener(final Class type, final ChildResourceListener listener) {
        //create ChildEventListener that wont throw exception if database has corrupted data
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    listener.onNewData(dataSnapshot.getValue(type));
                } catch (Exception err) {
                    FirebaseReporter.reportException(err, "Crash trying to get data from Firebase. Type: " + type.toString());
                    listener.onNewData(null);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try {
                    listener.onDataChanged(dataSnapshot.getValue(type));
                } catch (Exception err) {
                    FirebaseReporter.reportException(err, "Crash trying to get data from Firebase. Type: " + type.toString());
                    listener.onNewData(null);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // No op, don't need to do anything if a comment is removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                // No op, comments won't be moved.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // No op for now.
            }
        };
    }
}
