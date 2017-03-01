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

    private GameType gameType;

    private ResourceListener<List<Game>> privateGameGetter;
    private FirebaseGameResourceManager privateGameManager;
    private ResourceListener<List<Game>> publicGameGetter;
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
                Collections.shuffle(mixedGames);
                listener.onData(new ArrayList<>(mixedGames));
            }

            @Override
            public Class getDataType() {
                return Game.class;
            }
        };
        privateGameManager = new FirebaseGameResourceManager(0,
                privateLimit, privateGameGetter, GameType.PRIVATE_GAMES);

        publicGameGetter = new ResourceListener<List<Game>>() {
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
        if(privateGameGetter == null) {
            System.err.println("Call initManagerAndGetters() before retrieveGames()");
        }
        mixedGames.clear();
        if(gameType == GameType.MIXED_GAMES) {
            publicGameManager.retrievePublicGamesByPriority();
        } else if (gameType == GameType.PUBLIC_GAMES) {
            retrievePublicGamesByPriority();
        } else if (gameType == GameType.PRIVATE_GAMES) {
            retrieveUserPrivateGames();
        }

    }

    private void retrievePublicGamesByPriority() {
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

    private void retrieveUserPrivateGames() {
        String privatePath = String.format(USER_PRIVATE_GAMES, userId);
        Query query = database.getReference(privatePath).orderByPriority();

        if (retrievedPrivateOnce) {
            if(lastRetrievedPrivatePriority instanceof Double) {
                query = query.limitToFirst(privateLimit + 1)
                        .startAt((double) lastRetrievedPrivatePriority, lastRetrievedPrivateKey);
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
                    lastRetrievedPrivatePriority = snapshot.getPriority();
                    lastRetrievedPrivateKey = snapshot.getKey();
                    gameIds.add(lastRetrievedPrivateKey);
                }
                if (retrievedPrivateOnce) {
                    gameIds.remove(0);
                }
                else {
                    retrievedPrivateOnce = true;
                }
                convertIdsToGames(gameIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void convertIdsToGames(final List<String> gameIds) {
        final List<Game> privateGames = new ArrayList<>();
        for(final String gameId : gameIds) {
            DatabaseReference gameRef = database.getReference(GAME_TABLE + "/" + gameId);
            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    privateGames.add(dataSnapshot.getValue(Game.class));
                    if(dataSnapshot.getKey().equals(gameIds.get(gameIds.size() - 1))) {
                        listener.onData(privateGames);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(gameIds.size() == 0) {
            listener.onData(privateGames);
        }
    }
}
