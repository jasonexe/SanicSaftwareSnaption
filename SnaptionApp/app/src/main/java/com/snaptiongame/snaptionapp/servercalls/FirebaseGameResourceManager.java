package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.USER_DIRECTORY;
import static com.snaptiongame.snaptionapp.servercalls.FirebaseResourceManager.USER_PRIVATE_GAMES;

/**
 * FirebaseGameResourceManager is used to retrieve game data from Firebase
 *
 * @author Brittany Berlanga
 */

public class FirebaseGameResourceManager implements GameResourceManager {
    public static final String GAME_TABLE = "games";
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ResourceListener<List<Game>> listener;
    private List<Game> mixedGames;
    private int publicLimit;
    private int privateLimit;

    private boolean retrievedPublicOnce = false;
    private String lastRetrievedPublicKey;
    private String userId;
    private Object lastRetrievedPublicPriority;
    // Since we can pull public or private from this file, they need their own tracking variables
    private boolean retrievedPrivateOnce = false;
    private String lastRetrievedPrivateKey;
    private Object lastRetrievedPrivatePriority;


    /**
     * Constructor for the FirebaseGameResourceManager. Use this to get games from firebase.
     * If you want to pull more private games, create a new manager with a larger limit, and
     * use that to retrieve private games (and vice versa).
     *
     * @param publicLimit The number of public games to be retrieved
     * @param privateLimit The number of private games to be retrieved
     * @param listener A ResourceListener for retrieving a List of games
     */
    public FirebaseGameResourceManager(int publicLimit, int privateLimit, ResourceListener<List<Game>> listener) {
        this.publicLimit = publicLimit;
        this.privateLimit = privateLimit;
        this.listener = listener;
        userId = FirebaseResourceManager.getUserId();
        mixedGames = new ArrayList<>();
    }

    public void retrieveGames() {
        mixedGames.clear();
        final ResourceListener<List<Game>> privateGameGetter = new ResourceListener<List<Game>>() {
            @Override
            public void onData(List<Game> data) {
                mixedGames.addAll(data);
                // Since the limits will normally be relatively small, we can shuffle them
                // to give a little bit of a different order every time
                Collections.shuffle(mixedGames);
                listener.onData(new ArrayList<>(mixedGames));
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };
        final GameResourceManager privateGameManager = new FirebaseGameResourceManager(0,
                privateLimit, privateGameGetter);


        ResourceListener<List<Game>> publicGameGetter = new ResourceListener<List<Game>>() {
            @Override
            public void onData(List<Game> data) {
                mixedGames.addAll(data);
                privateGameManager.retrieveUserPrivateGames();
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };
        GameResourceManager publicGameManager = new FirebaseGameResourceManager(publicLimit,
                0, publicGameGetter);

        publicGameManager.retrievePublicGamesByPriority();
    }

    public void retrievePublicGamesByPriority() {
        Query query = database.getReference(GAME_TABLE).orderByPriority();
        if (retrievedPublicOnce) {
            if(lastRetrievedPublicPriority instanceof Double) {
                // endAt 0, any priority > 0 will be a private game, we don't want those per se.
                query = query.limitToFirst(publicLimit + 1)
                        .startAt((double) lastRetrievedPublicPriority, lastRetrievedPublicKey).endAt(0);
            }
        }
        else {
            query = query.limitToFirst(publicLimit);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Game> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        lastRetrievedPublicPriority = snapshot.getPriority();
                        lastRetrievedPublicKey = snapshot.getKey();
                        data.add((Game) snapshot.getValue(listener.getDataType()));
                    }
                    if (retrievedPublicOnce) {
                        data.remove(0);
                    }
                    else {
                        retrievedPublicOnce = true;
                    }
                }
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrievePublicGamesByPriority - " + databaseError.toString());
            }
        });
    }

    public void retrieveUserPrivateGames() {
        // If user isn't logged in, they won't have any games to return
        if(userId == null) {
            listener.onData(new ArrayList<Game>());
        }
        Query query = database.getReference(String.format(USER_PRIVATE_GAMES, userId)).orderByPriority();
        if (retrievedPrivateOnce) {
            if(lastRetrievedPrivatePriority instanceof Double) {
                query = query.limitToFirst(privateLimit + 1)
                        .startAt((double) lastRetrievedPrivatePriority, lastRetrievedPrivateKey);
            }
        }
        else {
            // Start at 0, since we only want privates
            query = query.limitToFirst(privateLimit).startAt(0);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Game> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        lastRetrievedPrivatePriority = snapshot.getPriority();
                        lastRetrievedPrivateKey = snapshot.getKey();
                        data.add((Game) snapshot.getValue(listener.getDataType()));
                    }
                    if (retrievedPrivateOnce) {
                        data.remove(0);
                    }
                    else {
                        retrievedPrivateOnce = true;
                    }
                }
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrievePublicGamesByPriority - " + databaseError.toString());
            }
        });
    }
}
