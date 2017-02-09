package com.snaptiongame.snaptionapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
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

import java.io.InputStream;
import java.net.URL;
import java.util.Observable;

import com.snaptiongame.snaptionapp.models.User;
import com.snaptiongame.snaptionapp.servercalls.ResourceListener;
import com.snaptiongame.snaptionapp.servercalls.Uploader;

import org.apache.commons.io.IOUtils;

/**
 * Handles logging in and logging out of Facebook and Google and connecting these services with
 * our backend
 *
 * Created by brittanyberlanga on 12/2/16.
 * Edited by Austin Robarts
 */
public class LoginManager extends Observable {
//TODO: refactor to remove Firebase objects from this class when we establish Uploader class
    public static final int GOOGLE_LOGIN_RC = 13; //request code used for Google Login Intent
    private static final String TAG = LoginManager.class.getSimpleName();
    private final String photosFolder = "ProfilePictures/";
    private final String photoExtension = ".jpg";
    private final String facebookImageUrl = "https://graph.facebook.com/%s/picture?type=large";

    private FirebaseAuth auth;
    private Uploader uploader;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private AuthCallback googleAuthCallback;
    private FragmentActivity activity;
    private boolean isLoggedIn;
    private byte[] profilePhoto;
    private LoginListener listener;

    public interface LoginListener {
        void onLoginComplete();
    }

    public LoginManager(FragmentActivity activity, Uploader uploader, LoginListener listener) {
        this.activity = activity;
        this.uploader = uploader;
        this.listener = listener;
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        isLoggedIn = auth.getCurrentUser() != null;
        profilePhoto = null;

        googleAuthCallback = new AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "GoogleAuth: SUCCESS");
            }

            @Override
            public void onError() {
                Log.d("TAG", "GoogleAuth: FAILURE");
            }
        };
    }

    public interface AuthCallback {
        void onSuccess();
        void onError();
    }

    public void loginWithGoogle() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        googleAuthCallback.onError();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        //This means that the result function will be triggered, override
        activity.startActivityForResult(signInIntent, GOOGLE_LOGIN_RC);
    }

    public void logoutOfGoogle(final AuthCallback authCallback) {
        if (googleApiClient != null && googleApiClient.isConnected() &&
                auth.getCurrentUser() != null) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                auth.signOut();
                                authCallback.onSuccess();
                            }
                            else {
                                authCallback.onError();
                            }
                        }
                    });
            googleApiClient.stopAutoManage(activity);
            googleApiClient.disconnect();
        }
        else {
            authCallback.onError();
        }
    }

    public void handleGoogleLoginResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            loginToFirebase(acct);
        } else {
            googleApiClient.disconnect();
        }
    }

    public void setupFacebookLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public String getStatus() {
        String status = "You are not logged in";
        String provider = getProvider();
        String userName = getUserName();
        if (provider != null && userName != null) {
            status = "You're logged into " + getProvider() + " as " + getUserName();
        }
        return status;
    }

    public boolean logOut() {
        //sign out of facebook
        com.facebook.login.LoginManager.getInstance().logOut();
        logoutOfGoogle(new AuthCallback() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "Successfully logged out of Google");
            }
            @Override
            public void onError() {
                Log.d("TAG", "Failed to log out of Google");
            }
        });
        auth.signOut();
        setLoggedIn(false);
        listener.onLoginComplete();
        return false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    private void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
        setChanged();
        notifyObservers();
    }

    private void uploadUser(final String facebookId) {
        FirebaseUser fbUser = auth.getCurrentUser();
        //make sure user is signed in before sending
        if (fbUser != null) {
            //establish fields needed for constructor
            final String id = fbUser.getUid();
            String imagePath = photosFolder + id + photoExtension;
            String email = fbUser.getEmail();
            String displayName = fbUser.getDisplayName();
            //TODO: fill this fields once we reach notifications and friends
            String notificationId = "";

            //getting facebook photo
            if (facebookId != null) {
                downloadPhoto(String.format(facebookImageUrl, facebookId));
            }
            //create and upload User to Firebase
            User user = new User(id, email, displayName, notificationId, facebookId, imagePath);
            uploader.addUser(user, profilePhoto, new ResourceListener<User>() {
                @Override
                public void onData(User data) {
                    listener.onLoginComplete();
                }

                @Override
                public Class getDataType() {
                    return User.class;
                }
            });
        }
    }

    private void downloadPhoto(final String imageUrl) {
        //have to use thread so it is not running on android's main thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = (InputStream)new URL(imageUrl).getContent();
                    profilePhoto = IOUtils.toByteArray(inputStream);
                }
                catch (Exception err) {
                    Log.d("TAG", "Loading Picture FAILED");
                    err.printStackTrace();
                }
            }
        });
        thread.start();
        //wait for thread to finish before going back to main execution
        //this is so we get the photo before we upload it
        try {
            thread.join();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    /**
     * Login with Google+
     * @param acct
     */
    private void loginToFirebase(final GoogleSignInAccount acct) {
        Uri photo = acct.getPhotoUrl();
        //downloading google profile picture
        downloadPhoto(photo.toString());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            uploadUser(null);
                            setLoggedIn(true);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            googleAuthCallback.onError();
                        }
                        googleAuthCallback = null;
                    }
                });
    }

    /**
     * Login with Facebook
     * @param token
     */
    private void loginToFirebase(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            uploadUser(token.getUserId());
                            setLoggedIn(true);
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }
                    }
                });
    }

    //TODO this should be pulled from firebase not the auth object
    private String getUserName() {
        String username = null;
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            username = user.getDisplayName();
        }
        return username;
    }

    private String getProvider() {
        String provider = null;
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            provider = user.getProviders().get(0);
        }
        return provider;
    }
}