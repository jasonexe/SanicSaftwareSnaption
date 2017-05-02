package com.snaptiongame.snaption.servercalls;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import com.google.firebase.auth.UserInfo;
import com.google.firebase.iid.FirebaseInstanceId;
import com.snaptiongame.snaption.Constants;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.models.User;
import com.snaptiongame.snaption.models.UserMetadata;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

/**
 * Handles logging in and logging out of Facebook and Google and connecting these services with
 * our backend, Firebase
 *
 * Created by brittanyberlanga on 12/2/16.
 * Edited by Austin Robarts
 */
public class LoginManager {
    private static final int LOGIN_GOOGLE_ID = 0;
    private static final String TAG = LoginManager.class.getSimpleName();
    private static final String FB_FRIENDS_PERMISSION = "user_friends";
    private static final String FB_EMAIL_PERMISSION = "email";
    private static final String FB_PROFILE_PERMISSION = "public_profile";
    private static final String PHOTOS_FOLDER = "ProfilePictures/";
    private static final String FACEBOOK_IMAGE_URL = "https://graph.facebook.com/%s/picture?type=large";

    private FirebaseAuth auth;
    private Uploader uploader;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;
    private AuthCallback loginAuthCallback;
    private AuthCallback logoutAuthCallback;
    private FragmentActivity activity;
    private byte[] profilePhoto;
    private LoginListener listener;

    public interface LoginListener {
        void onLoginComplete();
        void onLogoutComplete();
    }

    private FirebaseAuth.AuthStateListener revalidator = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser changedUser = firebaseAuth.getCurrentUser();
            if(changedUser != null) {
                String email = changedUser.getEmail();
                if(changedUser.getProviders() != null && changedUser.getProviders().size() > 0) {
                    String facebookId = null;
                    // Check if they are facebook. If so, need to set facebook ID
                    if(changedUser.getProviders().contains(FacebookAuthProvider.PROVIDER_ID)) {
                        // Users will usually have 2 providers in this list - firebase then
                        // Facebook or google
                        for(UserInfo info : changedUser.getProviderData()) {
                            if(info.getProviderId().equals(FacebookAuthProvider.PROVIDER_ID)) {
                                facebookId = info.getUid();
                            }
                        }
                    } else {
                        downloadPhoto(changedUser.getPhotoUrl().toString());
                    }
                    uploadUser(facebookId, email);
                }
            }
        }
    };

    /**
     * Constructor used when login and logout callbacks want to be defined on construction
     * @param activity activity used for GoogleAPI for managing
     * @param uploader to upload user to backend
     * @param listener callback for when login complete
     * @param loginAuthCallback called for succesful or unsuccessful login
     * @param logoutAuthCallback called for successful or unsuccessful logout
     */
    public LoginManager(FragmentActivity activity, final Uploader uploader, LoginListener listener,
                        AuthCallback loginAuthCallback, AuthCallback logoutAuthCallback) {
        this.activity = activity;
        this.uploader = uploader;
        this.listener = listener;
        this.loginAuthCallback = loginAuthCallback;
        this.logoutAuthCallback = logoutAuthCallback;
        auth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        profilePhoto = null;

        // This will make sure their stuff is uploaded, even if it was deleted from the database.
        // We could probably delete some other stuff and just use the state listener, but
        // I'm not sure what could be safely deleted.
        auth.addAuthStateListener(revalidator);
    }

    public interface AuthCallback {
        void onSuccess();
        void onError();
    }

    public void logOut() {
        //sign out of facebook
        com.facebook.login.LoginManager.getInstance().logOut();
        //sign out of google
        logoutOfGoogle();
        //sign out of firebase
        auth.signOut();
        //tell the view to update
        listener.onLogoutComplete();
    }

    public void loginWithGoogle() {
        resetGoogleApi();
        try {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(activity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // Build a GoogleApiClient with access to the Google Sign-In API and the
            // options specified by gso.
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, LOGIN_GOOGLE_ID, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            loginAuthCallback.onError();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            //This means that the result function will be triggered, override
            activity.startActivityForResult(signInIntent, Constants.GOOGLE_LOGIN_RC);
        }
        catch (Exception err) {
            FirebaseReporter.reportException(err, "Google login error");
            Log.d(TAG, "loginWithGoogle:" + err.getStackTrace().toString());
            loginAuthCallback.onError();
        }

    }

    public void handleFacebookLoginResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void logoutOfGoogle() {
        if (googleApiClient != null && googleApiClient.isConnected() &&
                auth.getCurrentUser() != null) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            googleApiClient.stopAutoManage(activity);
                            googleApiClient.disconnect();
                            if (status.isSuccess()) {
                                logoutAuthCallback.onSuccess();
                            }
                            else {
                                logoutAuthCallback.onError();
                            }
                        }
                    });
        }
    }

    public void handleGoogleLoginResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            // TODO remove this after we confirm that it works (need keys)
            System.out.println("Google email: " + acct.getEmail());
            loginToFirebase(acct);
        } else {
            resetGoogleApi();
        }
    }

    public void setupFacebookLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions(FB_EMAIL_PERMISSION, FB_PROFILE_PERMISSION, FB_FRIENDS_PERMISSION);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                loginToFirebase(loginResult.getAccessToken());
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
            }
            @Override
            public void onCancel() {
                loginAuthCallback.onError();
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                loginAuthCallback.onError();
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void resetGoogleApi() {
        if (googleApiClient != null) {
            googleApiClient.stopAutoManage(activity);
            googleApiClient.disconnect();
        }
    }

    private void uploadUser(String facebookId, String email) {
        FirebaseUser fbUser = auth.getCurrentUser();
        //make sure user is signed in before sending
        if (fbUser != null && email != null) {
            // Updates email so that if we want to, we can call FirebaseUser.getEmail() and it'll work
            fbUser.updateEmail(email);
            //establish fields needed for constructor
            String id = fbUser.getUid();
            String imagePath = PHOTOS_FOLDER + id;
            //String email = fbUser.getEmail();
            String displayName = fbUser.getDisplayName();
            String notificationId = FirebaseInstanceId.getInstance().getToken();

            //getting facebook photo
            if (facebookId != null) {
                downloadPhoto(String.format(FACEBOOK_IMAGE_URL, facebookId));
            }
            //create and upload User to Firebase
            UserMetadata user = new UserMetadata(displayName, email, imagePath, notificationId, facebookId, id);
            uploader.addUser(user, profilePhoto, new ResourceListener<UserMetadata>() {
                @Override
                public void onData(UserMetadata data) {
                    listener.onLoginComplete();
                }

                @Override
                public Class getDataType() {
                    return UserMetadata.class;
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
                    FirebaseReporter.reportException(err, "Loading profile picture failed");
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
        catch (InterruptedException err) {
            err.printStackTrace();
        }
    }

    /**
     * Login to Firebase with Google+
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
                            uploadUser(null, acct.getEmail());
                            loginAuthCallback.onSuccess();
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            loginAuthCallback.onError();
                        }
                    }
                });
    }

    private GraphRequest getFacebookEmailRequest(final AccessToken token) {
        return GraphRequest.newMeRequest(
                token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());
                        // Application code
                        try {
                            String email = object.getString("email");
                            uploadUser(token.getUserId(), email);
                            loginAuthCallback.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Login to Firebase with Facebook
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
                            GraphRequest request = getFacebookEmailRequest(token);
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "email");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            loginAuthCallback.onError();
                        }
                    }
                });
    }
}
