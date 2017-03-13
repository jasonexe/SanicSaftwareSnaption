package com.snaptiongame.snaptionapp.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaptionapp.Constants;
import com.snaptiongame.snaptionapp.models.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.data;
import static com.snaptiongame.snaptionapp.Constants.CREATION_DATE;
import static com.snaptiongame.snaptionapp.Constants.GAMES_PATH;
import static com.snaptiongame.snaptionapp.Constants.USER_PRIVATE_GAMES;

/**
 * FirebaseGameResourceManager is used to retrieve game data from Firebase
 *
 * @author Brittany Berlanga
 */

public class FirebaseGameResourceManager implements GameResourceManager {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ResourceListener<List<Game>> listener;
    private List<Game> privateGames;
    private int publicLimit;
    private int privateLimit;

    private boolean retrievedOnce = false;
    private String lastRetrievedKey;
    private Object lastRetrievedPriority;
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
    public FirebaseGameResourceManager(int publicLimit, int privateLimit, ResourceListener<List<Game>> listener, GameType gameType) {
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

    private void retrieveBottomPublicGames() {
        Query query = database.getReference(GAMES_PATH).orderByChild(CREATION_DATE);
        if(retrievedOnce) {
            // Technically last retrieved priority should be last retrieved creation date, but don't
            // Want to do extra variables
            query = query.limitToLast(publicLimit).endAt((long) lastRetrievedPriority, lastRetrievedKey);
        } else {
            query = query.limitToLast(publicLimit);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean continued = false;
                List<Game> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    boolean gotFirst = false;
                    for (DataSnapshot snapshot : snapshots) {
                        if(snapshot.getPriority() != null && (double) snapshot.getPriority() > 0) {
                            // If we had to do a continue, don't remove the last thing from data
                            continued = true;
                            continue;
                        }
                        continued = false;
                        Game curGame = (Game) snapshot.getValue(listener.getDataType());
                        if(!gotFirst) {
                            System.out.println("Setting last priority to " + curGame.getCreationDate());
                            lastRetrievedPriority = curGame.getCreationDate();
                            lastRetrievedKey = snapshot.getKey();
                            gotFirst = true;
                        }
                        data.add(curGame);
                    }
                    if (!retrievedOnce) {
                        retrievedOnce = true;
                    }
                }
                if(data.size() > 0 && !continued) {
                    data.remove(data.size() - 1);
                }
                // Since it still counts forwards, need to reverse it. IE if the list is 1 2 3 4 5,
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
        Query query = database.getReference(GAMES_PATH).orderByPriority();
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
                List<Game> data = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        lastRetrievedPriority = snapshot.getPriority();
                        lastRetrievedKey = snapshot.getKey();
                        data.add((Game) snapshot.getValue(listener.getDataType()));
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
                List<String> gameIds = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                for (DataSnapshot snapshot : snapshots) {
                    lastRetrievedPriority = snapshot.getPriority();
                    lastRetrievedKey = snapshot.getKey();
                    gameIds.add(lastRetrievedKey);
                }
                if (retrievedOnce) {
                    if (gameIds.size() > 0) {
                        gameIds.remove(0);
                    }
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
            DatabaseReference gameRef = database.getReference(String.format(Constants.GAME_PATH, gameIds.get(0)));
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
