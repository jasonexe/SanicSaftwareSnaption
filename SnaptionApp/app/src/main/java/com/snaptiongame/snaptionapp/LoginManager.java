package com.snaptiongame.snaptionapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Observable;

/**
 * Created by brittanyberlanga on 12/2/16.
 * Edited by Austin Robarts
 */
public class LoginManager extends Observable {

    public static final int GOOGLE_LOGIN_RC = 13; //request code used for Google Login Intent
    private static final String TAG = LoginManager.class.getSimpleName();
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private AuthCallback mGoogleAuthCallback;
    private FragmentActivity activity;
    private boolean isLoggedIn;

    public LoginManager(FragmentActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        isLoggedIn = mAuth.getCurrentUser() != null;
        System.out.println("IS LOGGED IN: " + isLoggedIn);
    }

    public interface AuthCallback {
        void onSuccess();
        void onError();
    }

    public void signInWithGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mGoogleAuthCallback.onError();
                        mGoogleAuthCallback = null;
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //This means that the result function will be triggered, override
        activity.startActivityForResult(signInIntent, GOOGLE_LOGIN_RC);


    }

    public void handleGoogleLoginResult(GoogleSignInResult result) {
        isLoggedIn = true;
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            loginToFirebase(acct);
        } else {
            mGoogleAuthCallback.onError();
            mGoogleAuthCallback = null;
        }
    }

    public void logoutOfGoogle(final AuthCallback authCallback) {
        if (mGoogleApiClient != null && mAuth.getCurrentUser() != null) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                mAuth.signOut();
                                authCallback.onSuccess();
                            }
                            else {
                                authCallback.onError();
                            }
                        }
                    });
        }
        else {
            authCallback.onError();
        }
    }



    public void setupFacebookLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                loginToFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }


    public void handleFacebookLoginResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String getStatus() {
        String status = "";
        String provider = getProvider();
        String userName = getUserName();
        if (provider != null && userName != null) {
            status += "You're logged into " + getProvider() + " as " + getUserName();
        }
        else {
            status += "Logout unsuccessful";
        }

        return status;
    }

    //TODO this should be pulled from firebase not the auth object
    private String getUserName() {
        String username = null;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            username = user.getDisplayName();
        }
        return username;
    }

    private String getProvider() {
        String provider = null;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            provider = user.getProviders().get(0);
        }
        return provider;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    private void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        setChanged();
        notifyObservers();
    }

    /**
     * Login with Google+
     * @param acct
     */
    private void loginToFirebase(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            setLoggedIn(true);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            mGoogleAuthCallback.onError();
                        }
                        mGoogleAuthCallback = null;
                    }
                });
    }

    /**
     * Login with Facebook
     * @param token
     */
    private void loginToFirebase(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            setLoggedIn(true);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                    }
                });
    }
}