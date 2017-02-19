package com.snaptiongame.snaptionapp.servercalls;

import android.text.TextUtils;

import com.google.firebase.crash.FirebaseCrash;

/**
 * Class to report stuff to firebase using FirebaseCrash SDK
 * Created by Jason Krein on 2/17/2017.
 */

public class FirebaseReporter {
    /**
     * Reports the exception to firebase
     * @param e the exception to report
     * @param message The optional message to give to firebase
     */
    public static void reportException(Throwable e, String message) {
        FirebaseCrash.report(e);
        if(!TextUtils.isEmpty(message)) {
            FirebaseCrash.log(message);
        }
    }
}
