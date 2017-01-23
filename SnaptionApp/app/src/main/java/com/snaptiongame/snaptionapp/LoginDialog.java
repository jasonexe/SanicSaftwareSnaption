package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    protected TextView mStatusTextView;
    private Activity activity;
    private LoginManager manager;
    private SignInButton googleLogButton;
    private LoginButton facebookLogButton;
    private LoginManager.AuthCallback loginAuthCallback;
    private LoginManager.AuthCallback logoutAuthCallback;

    public LoginDialog(Activity activity, LoginManager manager) {
        super(activity);
        this.activity = activity;
        this.manager = manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        //create google sign in button
        googleLogButton = (SignInButton)findViewById(R.id.google_login_button);
        googleLogButton.setSize(SignInButton.SIZE_WIDE);
        //create listener for google sign in
        googleLogButton.setOnClickListener(this);
        //create facebook sign in button
        facebookLogButton = (LoginButton) findViewById(R.id.facebook_login_button);

        mStatusTextView = (TextView)findViewById(R.id.login_status_text);

        setUpLoginCallbacks();
        updateStatus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.google_login_button:
                manager.signInWithGoogle();
                break;
        }
    }

    private void setUpLoginCallbacks() {
        loginAuthCallback = new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                updateStatus();
            }

            @Override
            public void onError() {
                mStatusTextView.setText("Something went wrong");
            }
        };
        logoutAuthCallback = new LoginManager.AuthCallback() {
            @Override
            public void onSuccess() {
                mStatusTextView.setText("Successfully logged out");
            }

            @Override
            public void onError() {
                mStatusTextView.setText("Unable to logout");
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
}
