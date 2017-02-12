package com.snaptiongame.snaptionapp.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.snaptiongame.snaptionapp.LoginManager;
import com.snaptiongame.snaptionapp.MainSnaptionActivity;
import com.snaptiongame.snaptionapp.R;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {

    private LoginManager manager;
    private SignInButton googleLogButton;
    private LoginButton facebookLogButton;



    public LoginDialog(Activity activity, LoginManager manager) {
        super(activity);
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
        manager.setupFacebookLoginButton(facebookLogButton);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            //cases will be added here to deal with logout
            case R.id.google_login_button:
                manager.loginWithGoogle();
                break;
        }
    }
}
