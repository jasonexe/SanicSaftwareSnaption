package com.snaptiongame.snaptionapp.servercalls;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Class for upload functions to firebase
 */

public class FirebaseUpload {

    public void uploadString(String firebasePath, String content) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.child("Tests").setValue("WTFFF");
        System.out.println("Did something");
    }
}
