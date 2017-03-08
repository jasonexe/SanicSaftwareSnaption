package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<Game> privateGames;
    private int publicLimit;
    private int privateLimit;

    private boolean retrievedOnce = false;
    private String lastRetrievedKey;
    private Object lastRetrievedPriority;
    private String userId;

    private GameType gameType;

    private ResourceListener<List<Game>> privateGameGetter;
    private FirebaseGameResourceManager privateGameManager;
    private FirebaseGameResourceManager publicGameManager;


    /**
     * Constructor for the FirebaseGameResourceManager. Use this to get games from firebase.
     * If you want to pull more private games, create a new manager with a larger limit, and
     * use that to retrieve private games (and vice versa).
     *
     * @param publicLimit The number of public games to be retrieved
     * @param privateLimit The number of private games to be retrieved
     * @param listener A ResourceListener for retrieving a List of games
     * @param gameType Enum describing if you want mixed games, only private, or only public
     */
    public FirebaseGameResourceManager(int publicLimit, int privateLimit, ResourceListener<List<Game>> listener, GameType gameType) {
        this.publicLimit = publicLimit;
        this.privateLimit = privateLimit;
        this.listener = listener;
        this.gameType = gameType;
        userId = FirebaseResourceManager.getUserId();
        mixedGames = new ArrayList<>();

        if(gameType == GameType.MIXED_GAMES) {
            initManagerAndGetters();
        }
    }

    private void initManagerAndGetters() {
        privateGameGetter = new ResourceListener<List<Game>>() {
            @Override
            public void onData(List<Game> data) {
                mixedGames.addAll(data);
                // Since the limits will normally be relatively small, we can shuffle them
                // to give a little bit of a different order every time
                // comment out for now to demonstrate ordering in app
//                Collections.shuffle(mixedGames);
                listener.onData(new ArrayList<>(mixedGames));
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };
        privateGameManager = new FirebaseGameResourceManager(0,
                privateLimit, privateGameGetter, GameType.PRIVATE_GAMES);

        ResourceListener<List<Game>> publicGameGetter = new ResourceListener<List<Game>>() {
            @Override
            public void onData(List<Game> data) {
                mixedGames.addAll(data);
                privateGameManager.retrieveGames();
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };
        publicGameManager = new FirebaseGameResourceManager(publicLimit,
                0, publicGameGetter, GameType.PUBLIC_GAMES);
    }



    public void retrieveGames() {
        mixedGames.clear();
        if (gameType == GameType.MIXED_GAMES) {
            publicGameManager.retrievePublicGamesByPriority();
        } else if (gameType == GameType.PUBLIC_GAMES) {
            retrievePublicGamesByPriority();
        } else if (gameType == GameType.PRIVATE_GAMES) {
            retrieveUserPrivateGames();
        }

    }

    private void retrievePublicGamesByPriority() {
        Query query = database.getReference(GAME_TABLE).orderByPriority();
        if (retrievedOnce) {
            if (lastRetrievedPriority instanceof Double) {
                // endAt 0, any priority > 0 will be a private game, we don't want those per se.
                query = query.limitToFirst(publicLimit + 1)
                        .startAt((double) lastRetrievedPriority, lastRetrievedKey).endAt(0);
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
                        lastRetrievedPriority = snapshot.getPriority();
                        lastRetrievedKey = snapshot.getKey();
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
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrievePublicGamesByPriority - " + databaseError.toString());
            }
        });
    }

    private void retrieveUserPrivateGames() {
        String privatePath = String.format(USER_PRIVATE_GAMES, userId);
        Query query = database.getReference(privatePath).orderByPriority();

        if (retrievedOnce) {
            if (lastRetrievedPriority instanceof Double) {
                query = query.limitToFirst(privateLimit + 1)
                        .startAt((double) lastRetrievedPriority, lastRetrievedKey);
            }
        }
        else {
            query = query.limitToFirst(privateLimit);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> gameIds = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : snapshots) {
                    lastRetrievedPriority = snapshot.getPriority();
                    lastRetrievedKey = snapshot.getKey();
                    gameIds.add(lastRetrievedKey);
                }
                if (retrievedOnce) {
                    gameIds.remove(0);
                }
                else {
                    retrievedOnce = true;
                }
                convertIdsToGames(gameIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrievePrivateGamesByPriority - " + databaseError.toString());
                FirebaseReporter.reportException(null, "Error getting private database info");
                listener.onData(null);
            }
        });
    }

    private void convertIdsToGames(List<String> gameIds) {
        privateGames = new ArrayList<>();
        convertIdsToGamesHelper(gameIds);
    }

    private void convertIdsToGamesHelper(final List<String> gameIds) {
        // If this is last game, don't recursive
        if (gameIds.size() == 0) {
            listener.onData(privateGames);
        } else {
            DatabaseReference gameRef = database.getReference(GAME_TABLE + "/" + gameIds.get(0));
            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    privateGames.add(dataSnapshot.getValue(Game.class));
                    gameIds.remove(0);
                    convertIdsToGamesHelper(gameIds);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
