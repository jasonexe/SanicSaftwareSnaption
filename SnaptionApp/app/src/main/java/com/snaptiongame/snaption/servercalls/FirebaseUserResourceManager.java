package com.snaptiongame.snaption.servercalls;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by austinrobarts on 4/22/17.
 */

public class FirebaseUserResourceManager extends FirebaseResourceManager {

    public static void getUserById(String id, ResourceListener listener) {

    }

    public static void getUserPrivateData(String id, ResourceListener listener) {

    }

    public static void getUserPublicData(String id, ResourceListener listener) {

    }

    public static void getUserMetadata(String id, ResourceListener listener) {

    }

    public static void getUserMetadataByFacebookId(String id, ResourceListener listener) {

    }

    /**
     * Loads a list of users based on the start of their display name and/or e-mail.
     *
     * @param begin the name/e-mail to be searched for
     * @param path the path to the child to determine what the query looks for, being name/e-mail
     * @param listener ResourceListener the users are returned to
     */
    public static void getUserMetadataByName(String begin, String path, final ResourceListener<List<User>> listener) {
        Query query = database.getReference(Constants.USERS_METADATA_PATH).orderByChild(path).startAt(begin).endAt(begin + "~");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();
                if (snapshots.iterator().hasNext()) {
                    for (DataSnapshot snapshot : snapshots) {
                        users.add((User) snapshot.getValue(listener.getDataType()));
                    }
                }
                listener.onData(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(FirebaseGameResourceManager.class.getSimpleName(), "retrieveUsersByName - " + databaseError.toString());
                listener.onData(null);
            }
        });
    }


}
