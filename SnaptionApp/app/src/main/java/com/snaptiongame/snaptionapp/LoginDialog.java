package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener, Observer {

    private LoginManager manager;
    private SignInButton googleLogButton;
    private LoginButton facebookLogButton;

    public LoginDialog(Activity activity, LoginManager manager) {
        super(activity);
        this.manager = manager;
        manager.addObserver(this);
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
        manager.setupFacebookLoginButton(facebookLogButton);
    }

    /**
     * Update when login status changes
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        //dismisses the dialog to go back to main screen
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //cases will be added here to deal with logout
            case R.id.google_login_button:
                manager.signInWithGoogle();
                break;
        }
    }
}
