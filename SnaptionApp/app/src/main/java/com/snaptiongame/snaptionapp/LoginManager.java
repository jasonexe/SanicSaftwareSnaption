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

/**
 * Created by brittanyberlanga on 12/2/16.
 */

public class LoginManager {
    public static final int GOOGLE_LOGIN_RC = 13;
    private static final String TAG = LoginManager.class.getSimpleName();
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mCallbackManager;
    private AuthCallback mGoogleAuthCallback;
    private AuthCallback mFacebookAuthCallback;
    private AuthCallback mFacebookLogoutAuthCallback;
    private FragmentActivity activity;
    private AccessTokenTracker accessTokenTracker;

    public LoginManager(FragmentActivity activity) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
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
    }

    public interface AuthCallback {
        void onSuccess();
        void onError();
    }

    public void loginToGoogle(final AuthCallback authCallback) {
        this.mGoogleAuthCallback = authCallback;
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        //This means that the result function will be triggered, override
        activity.startActivityForResult(signInIntent, GOOGLE_LOGIN_RC);
    }

    public void handleGoogleLoginResult(GoogleSignInResult result) {
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



    public void setupFacebookLoginButton(LoginButton loginButton, AuthCallback loginCallback, AuthCallback logoutCallback) {
        this.mFacebookAuthCallback = loginCallback;
        this.mFacebookLogoutAuthCallback = logoutCallback;
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
                mFacebookAuthCallback.onError();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                mFacebookAuthCallback.onError();
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                // the newAccessToken becomes null when the user signs out
                if (newAccessToken == null) {
                    mFacebookLogoutAuthCallback.onSuccess();
                }
            }
        };
    }


    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //TODO this should be pulled from firebase not the auth object
    public String getUserName() {
        String username = null;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            username = user.getDisplayName();
        }
        return username;
    }

    public String getProvider() {
        String provider = null;
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            provider = user.getProviders().get(0);
        }
        return provider;
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
                            //mGoogleAuthCallback.onSuccess();
                            System.out.println("LOGGED IN TO FIREBASE BOIIII");
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
                            mFacebookAuthCallback.onSuccess();
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            mFacebookAuthCallback.onError();
                        }
                    }
                });
    }
}