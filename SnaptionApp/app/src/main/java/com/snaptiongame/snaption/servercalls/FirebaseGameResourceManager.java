package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.models.Game;
import com.snaptiongame.snaption.models.GameMetaData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.snaptiongame.snaption.Constants.CREATION_DATE;
import static com.snaptiongame.snaption.Constants.GAMES_PATH;
import static com.snaptiongame.snaption.Constants.GAMES_PUBLIC_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.GAME_METADATA_PATH;
import static com.snaptiongame.snaption.Constants.JOINED_GAMES_PATH;
import static com.snaptiongame.snaption.Constants.USER_PRIVATE_GAMES;

/**
 * FirebaseGameResourceManager is used to retrieve game data from Firebase
 *
 * @author Brittany Berlanga
 */

public class FirebaseGameResourceManager implements GameResourceManager {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ResourceListener<List<GameMetaData>> listener;
    private List<GameMetaData> privateGames;
    private int publicLimit;
    private int privateLimit;

    private boolean retrievedOnce = false;
    private String lastRetrievedKey;
    private Object lastRetrievedPriority;
    private Object lastRetrievedCreationDate;
    private String userId;

    private GameType gameType;

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
    public FirebaseGameResourceManager(int publicLimit, int privateLimit, ResourceListener<List<GameMetaData>> listener, GameType gameType) {
        this.publicLimit = publicLimit;
        this.privateLimit = privateLimit;
        this.listener = listener;
        this.gameType = gameType;
        userId = FirebaseResourceManager.getUserId();
    }

    public void retrieveGames() {
        // If we're getting the games they joined, just call private games
        if (gameType == GameType.USER_JOINED_GAMES) {
            retrieveUserPrivateGames();
        } else if (gameType == GameType.TOP_PUBLIC_GAMES){
            // Otherwise, we're getting either random or top public games, so check which.
            retrievePublicGamesByPriority();
        } else if (gameType == GameType.UNPOPULAR_PUBLIC_GAMES) {
            retrieveBottomPublicGames();
        }
    }

    // This method retrieves games in the order they are created, from newest to oldest
    private void retrieveBottomPublicGames() {
        Query query = database.getReference(Constants.GAMES_PUBLIC_METADATA_PATH).orderByChild(CREATION_DATE);
        if (retrievedOnce) {
            query = query.limitToLast(publicLimit).endAt((long) lastRetrievedCreationDate, lastRetrievedKey);
        } else {
            query = query.limitToLast(publicLimit);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean continued = false;
                List<GameMetaData> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    // Since we're using the first game's creation date and key, this boolean is
                    // to track if they have been set yet.
                    boolean gotFirst = false; 
                    for (DataSnapshot snapshot : snapshots) {
                        GameMetaData curGame = (GameMetaData) snapshot.getValue(listener.getDataType());
                        if(!gotFirst) {
                            lastRetrievedCreationDate = curGame.getCreationDate();
                            lastRetrievedKey = snapshot.getKey();
                            gotFirst = true;
                        }
                        // Checks if the game is private or not. If it is, then the priority will be > 0, meaning don't put it in data
                        if(snapshot.getPriority() != null && (double) snapshot.getPriority() > 0) {
                            // If we had to do a continue, don't remove the last thing from data
                            continued = true;
                            continue;
                        }
                        // This is only set to false if the above if statement is false. This and the above logic makes it
                        // so that if the last game retrieved was private, then we won't remove the game retrieved before it
                        // from the list.
                        continued = false;
                        data.add(curGame);
                    }
                    // If the last retrieved game was not private, and this is not
                    // the first query then remove it (it will be retrieved in the next query)
                    if(data.size() > 0 && !continued && retrievedOnce) {
                        data.remove(data.size() - 1);
                    }

                    if (!retrievedOnce) {
                        retrievedOnce = true;
                    }
                }

                // Since the query still counts forwards, need to reverse it. IE if the list is 1 2 3 4 5,
                // and we limitToLast 2, we'll get back 4 5, but want it in 5 4 order. But still
                // endAt 4 for the next query, which is why we have gotFirst above.
                Collections.reverse(data);
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void retrievePublicGamesByPriority() {
        Query query = database.getReference(GAMES_PUBLIC_METADATA_PATH).orderByPriority();
        if (retrievedOnce) {
            if (lastRetrievedPriority instanceof Double) {
                // endAt 0, any priority > 0 will be a private game, we don't want those per se.
                query = query.limitToFirst(publicLimit + 1)
                        .startAt((double) lastRetrievedPriority, lastRetrievedKey).endAt(0);
            }  else {
                // If for some reason last priority isn't set to double, we can still use the key
                // Start at min double value so we know we aren't missing any
                query = query.limitToFirst(privateLimit + 1)
                        .startAt(Double.MIN_VALUE, lastRetrievedKey).endAt(0);
            }
        }
        else {
            query = query.limitToFirst(publicLimit).endAt(0);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GameMetaData> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        lastRetrievedPriority = snapshot.getPriority();
                        lastRetrievedKey = snapshot.getKey();
                        data.add((GameMetaData) snapshot.getValue(listener.getDataType()));
                    }
                    if (retrievedOnce) {
                        if (data.size() > 0) {
                            data.remove(0);
                        }
                    }
                    else {
                        retrievedOnce = true;
                    }
                }
                listener.onData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(),
                        "retrievePublicGamesByPriority - " + databaseError.toString());
            }
        });
    }

    private void retrieveUserPrivateGames() {
        String privatePath = String.format(JOINED_GAMES_PATH, userId);
        Query query = database.getReference(privatePath).orderByPriority();

        if (retrievedOnce) {
            if (lastRetrievedPriority instanceof Double) {
                query = query.limitToFirst(privateLimit + 1)
                        .startAt((double) lastRetrievedPriority, lastRetrievedKey);
            } else {
                // If for some reason last priority isn't set to double, we can still use the key
                // Start at min double value so we know we aren't missing any
                query = query.limitToFirst(privateLimit + 1).startAt(Double.MIN_VALUE, lastRetrievedKey);
            }
        }
        else {
            query = query.limitToFirst(privateLimit);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> gameIds = new LinkedHashMap<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : snapshots) {
                    lastRetrievedPriority = snapshot.getPriority();
                    lastRetrievedKey = snapshot.getKey();
                    gameIds.put(lastRetrievedKey, (String)snapshot.getValue());
                }
                if (retrievedOnce) {
                    if (gameIds.size() > 0) {
                        gameIds.remove(0);
                    }
                }
                else {
                    retrievedOnce = true;
                }
                //Take the game id map and convert them to games
                convertIdsToGames(gameIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(),
                        "retrievePrivateGamesByPriority - " + databaseError.toString());
                FirebaseReporter.reportException(null, "Error getting private database info");
                listener.onData(null);
            }
        });
    }

    /**
     * Takes a map of game IDs and their accessibility strings and converts them to a list of games.
     *
     * @param gameIds A map of game IDs and their accessibility strings
     */
    private void convertIdsToGames(Map<String, String> gameIds) {
        privateGames = new ArrayList<>();
        convertIdsToGamesHelper(new ArrayList<>(gameIds.keySet()), gameIds);
    }

    /**
     * A recursive function to help convert the map of game ids to a list of games. It removes a key
     * for each iteration.
     *
     * @param keys A list of game IDs
     * @param gameIds A map of game IDs and their accessibility strings
     */
    private void convertIdsToGamesHelper(final List<String> keys, final Map<String, String> gameIds) {
        // If this is last game, don't recursive
        if (keys.size() == 0) {
            listener.onData(privateGames);
        } else {
            //Create a path from the accessibility and the game id
            String gamePath = String.format(GAME_METADATA_PATH,
                    gameIds.get(keys.get(0)), keys.get(0));
            DatabaseReference gameRef = database.getReference(gamePath);
            gameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    privateGames.add(dataSnapshot.getValue(GameMetaData.class));
                    keys.remove(0);
                    convertIdsToGamesHelper(keys, gameIds);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}
