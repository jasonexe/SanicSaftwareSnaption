package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.login.widget.LoginButton;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    protected TextView mStatusTextView;
    private Activity activity;
    private LoginManager manager;
    private Button googleLogButton;
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
        googleLogButton = (Button)findViewById(R.id.goog_sign_in_button);
        //create listener for google sign in
        googleLogButton.setOnClickListener(this);
        //create facebook sign in button
        facebookLogButton = (LoginButton) findViewById(R.id.login_button);

        mStatusTextView = (TextView)findViewById(R.id.login_status_text);

        setUpLoginCallbacks();
        updateStatus();
        updateGoogleSignInButton();
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
            googleLogButton.setText( activity.getString(R.string.sign_out));
        }
        else {
            googleLogButton.setText(activity.getString(R.string.sign_in));
        }
    }
}
