package com.snaptiongame.snaptionapp.servercalls;

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
        if(message != null && !message.isEmpty()) {
            FirebaseCrash.log(message);
        }
    }
}
