package com.snaptiongame.snaption.ui.login;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.login.widget.LoginButton;
import com.snaptiongame.snaption.R;
import com.snaptiongame.snaption.servercalls.LoginManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Dialog that is displayed when a user is trying to log in.
 * It will offer an option for Facebook or Google+ login
 *
 * Created by austinrobarts on 1/16/17.
 */

public class LoginDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private LoginManager manager;
    private Button googleLogButton;
    private LoginButton facebookLogButton;

    /**
     * Constructor used when LoginManager must be set after construction
     * @param activity current activity where dialog will be displayed
     */
    public LoginDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    /**
     * Constructor used when LoginManger can be created before construction
     * @param activity current activity where dialog will be displayed
     * @param manager loginManager to handle logging in on button click
     */
    public LoginDialog(Activity activity, LoginManager manager) {
        super(activity);
        this.activity = activity;
        this.manager = manager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);
        ButterKnife.bind(this);

        //create google sign in button
        googleLogButton = (Button)findViewById(R.id.google_login_button);
        //create listener for google sign in
        googleLogButton.setOnClickListener(this);
        //create facebook sign in button
        facebookLogButton = (LoginButton) findViewById(R.id.facebook_login_button);
        if (manager != null) {
            manager.setupFacebookLoginButton(facebookLogButton);
        }
        else {
            Log.d("LoginDialog", "onCreate: LoginManager never was assigned");
        }
    }



    @OnClick(R.id.mock_facebook_login_button)
    public void onClickMockFacebookButton() {
        facebookLogButton.performClick();
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            //only needs to handle login for google here
            case R.id.google_login_button:
                if (manager != null) {
                    manager.loginWithGoogle();
                }
                else {
                    Log.d("LoginDialog", "onCreate: LoginManager never was assigned");
                }

                break;
        }
    }

    public void setLoginManager(LoginManager manager) {
        this.manager = manager;
    }

    public void showPostLogDialog(String text) {
        dismiss();
        Snackbar.make(activity.getCurrentFocus(),text, Snackbar.LENGTH_LONG).show();
    }
}
