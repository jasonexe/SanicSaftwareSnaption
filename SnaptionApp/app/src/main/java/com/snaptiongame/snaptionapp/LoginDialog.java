package com.snaptiongame.snaptionapp;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener, Observer {

    private LoginManager manager;
    private LoginListener loginListener;
    private SignInButton googleLogButton;
    private LoginButton facebookLogButton;

    public interface LoginListener {
        void onLoginComplete();
    }

    public LoginDialog(Activity activity, LoginManager manager, LoginListener loginListener) {
        super(activity);
        this.manager = manager;
        this.loginListener = loginListener;
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
        // notify listener
        if (loginListener != null) {
            loginListener.onLoginComplete();
        }
        //dismisses the dialog to go back to main screen
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //cases will be added here to deal with logout
            case R.id.google_login_button:
                manager.loginWithGoogle();
                break;
        }
    }
}
