package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    private LoginManager.AuthCallback mFacebookAuthCallback;
    private LoginManager.AuthCallback mFacebookLogoutAuthCallback;
    private static final String TAG = LoginManager.class.getSimpleName();
    private AccessTokenTracker accessTokenTracker;
    private FirebaseAuth mAuth;
    private FragmentActivity activity;
    protected TextView mStatusTextView;
    private LoginManager manager;
    private Button googleLogButton;
    private LoginButton facebookLogButton;
    private LoginManager.AuthCallback loginAuthCallback;
    private LoginManager.AuthCallback logoutAuthCallback;

    public LoginDialog(FragmentActivity activity, LoginManager manager) {
        super(activity);
        this.activity = activity;
        this.manager = manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        //placing google sign in button
        googleLogButton = (Button)findViewById(R.id.goog_sign_in_button);
        googleLogButton.setOnClickListener(this);
        mStatusTextView = (TextView)findViewById(R.id.login_status_text);


        facebookLogButton = (LoginButton) findViewById(R.id.login_button);

        CallbackManager callbackManager = CallbackManager.Factory.create();
        facebookLogButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        setUpLoginCallbacks();
        updateStatus();
        updateGoogleSignInButton();
    }




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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.goog_sign_in_button:
                if (googleLogButton.getText().equals("Sign out")) {
                    manager.logoutOfGoogle(logoutAuthCallback);
                }
                else {
                    manager.loginToGoogle(loginAuthCallback);
                }
                break;
        }
    }

    private void setUpLoginCallbacks() {
        loginAuthCallback = new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                updateStatus();
                updateGoogleSignInButton();
            }

            @Override
            public void onError() {
                mStatusTextView.setText("Something went wrong");
                updateGoogleSignInButton();
            }
        };
        logoutAuthCallback = new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                mStatusTextView.setText("Successfully logged out");
                updateGoogleSignInButton();
            }

            @Override
            public void onError() {
                mStatusTextView.setText("Unable to logout");
                updateGoogleSignInButton();
            }
        };
        manager.setupFacebookLoginButton(facebookLogButton, loginAuthCallback, logoutAuthCallback);
    }

    private void updateStatus() {
        String status = "";
        if (!TextUtils.isEmpty(manager.getUserName())) {
            status += "You're logged into " + manager.getProvider() + " as " + manager.getUserName();
        }
        mStatusTextView.setText(status);
    }

    private void updateGoogleSignInButton() {
        if (!TextUtils.isEmpty(manager.getUserName()) && manager.getProvider().equals("google.com")) {
            googleLogButton.setText("Sign out");
        }
        else {
            googleLogButton.setText("Sign in");
        }
    }
}
