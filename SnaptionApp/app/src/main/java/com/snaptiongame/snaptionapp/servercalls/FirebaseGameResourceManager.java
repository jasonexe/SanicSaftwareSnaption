package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.models.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * FirebaseGameResourceManager is used to retrieve game data from Firebase
 *
 * @author Brittany Berlanga
 */

public class FirebaseGameResourceManager implements GameResourceManager {
    public static final String GAME_TABLE = "games";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String CREATION_DATE_CHILD = "creationDate";
    private ResourceListener<List<Game>> listener;
    private int limit;
    private boolean retrievedOnce = false;
    private String lastRetrievedKey;
    private Long lastRetrievedDate;

    /**
     * Constructor for the FirebaseGameResourceManager
     *
     * @param limit The number of games to be retrieved
     * @param listener A ResourceListener for retrieving a List of games
     */
    public FirebaseGameResourceManager(int limit, ResourceListener<List<Game>> listener) {
        this.limit = limit;
        this.listener = listener;
    }

    public void retrieveGamesByCreationDate() {
        Query query = database.getReference(GAME_TABLE).orderByChild(CREATION_DATE_CHILD);
        if (retrievedOnce) {
            query = query.limitToFirst(limit + 1).startAt(lastRetrievedDate, lastRetrievedKey);
        }
        else {
            query = query.limitToFirst(limit);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Game> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        lastRetrievedKey = snapshot.getKey();
                        lastRetrievedDate = (Long) snapshot.child(CREATION_DATE_CHILD).getValue();
                        data.add((Game) snapshot.getValue(listener.getDataType()));
                    }
                    if (retrievedOnce) {
                        data.remove(0);
                    }
                    else {
                        retrievedOnce = true;
                    }
                }
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrieveGamesByCreationDate - " + databaseError.toString());
            }
        });
    }
}
