package com.slc.android.sceneliner.app;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.slc.android.sceneliner_1_0.R;
import com.slc.android.sceneliner.control.AppController;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends ActionBarActivity {

    private EditText loginScreenUsernameEditText;
    private EditText loginScreenPwEditText;
    private EditText signupScreenUserNameEditText;
    private EditText signupScreenPwEditText;
    private EditText signupScreenPwVerifyEditText;
    private EditText signupScreenEmailEditText;
    private TextView loginScreenSignUpTextView;
    private Button loginScreenLoginButton;
    private Button signupScreenRegisterButton;

    private ViewFlipper loginActivityViewFlipper;

    private LoginActivity thisActivity;

    Handler delayForLoginCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        thisActivity = this;

        delayForLoginCallback = new Handler();

        loginActivityViewFlipper = (ViewFlipper) findViewById(R.id.loginFlipper);

        loginScreenUsernameEditText = (EditText) findViewById(R.id.loginScreenUserNameEditText);
        loginScreenPwEditText = (EditText) findViewById(R.id.loginScreenPwEditText);
        loginScreenLoginButton = (Button) findViewById(R.id.loginScreenLoginButton);
        loginScreenSignUpTextView = (TextView) findViewById(R.id.loginScreenRegisterTextView);


        loginScreenUsernameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    loginScreenPwEditText.requestFocus();
                    return true;
                } else
                    return false;
            }
        });


        loginScreenPwEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    loginToApp();
                    return true;
                } else
                    return false;
            }
        });


        loginScreenSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipToSignUpScreen();
            }
        });


        loginScreenLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToApp();
            }
        });

        signupScreenUserNameEditText = (EditText) findViewById(R.id.createAccountScreenUserNameEditText);
        signupScreenUserNameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    signupScreenEmailEditText.requestFocus();
                    return true;
                } else
                    return false;
            }
        });

        signupScreenEmailEditText = (EditText) findViewById(R.id.createAccountScreenEmailEditText);
        signupScreenEmailEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    signupScreenPwEditText.requestFocus();
                    return true;
                } else
                    return false;
            }
        });

        signupScreenPwEditText = (EditText) findViewById(R.id.createAccountScreenPwEditText);
        signupScreenPwEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    signupScreenPwVerifyEditText.requestFocus();
                    return true;
                } else
                    return false;
            }
        });

        signupScreenPwVerifyEditText = (EditText) findViewById(R.id.createAccountScreenPwVerifyEditText);
        signupScreenPwVerifyEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    registerForApp();
                    return true;
                } else
                    return false;
            }
        });




        signupScreenRegisterButton = (Button) findViewById(R.id.createAccountScreenCreateAccountButton);
        signupScreenRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForApp();
            }
        });

    }

    private void registerForApp() {
        String user = signupScreenUserNameEditText.getText().toString().toLowerCase();
        String pw = signupScreenPwEditText.getText().toString();
        String pwVerify = signupScreenPwVerifyEditText.getText().toString();
        String email = signupScreenEmailEditText.getText().toString();
        AppController.createAccount(user, pw, pwVerify, email);
        delayForLoginCallback.postDelayed(new DelayForLogin(), 500);
    }

    private void loginToApp() {
        String user = loginScreenUsernameEditText.getText().toString().toLowerCase();
        String pw = loginScreenPwEditText.getText().toString();
        AppController.userLogin(user, pw);
        delayForLoginCallback.postDelayed(new DelayForLogin(), 500);
    }

    private void flipToSignUpScreen() {
        if (loginActivityViewFlipper.getDisplayedChild() == 0) {
            loginActivityViewFlipper.setInAnimation(this, R.anim.abc_slide_in_top);
            loginActivityViewFlipper.setOutAnimation(this, R.anim.abc_slide_out_bottom);
            loginActivityViewFlipper.showNext();
        } else {
            Log.e("SL_LOGIN_ACTIVITY", "Attempted to flip to sign up screen but are already on it!");
        }
    }

    private void flipToLoginScreen() {
        if (loginActivityViewFlipper.getDisplayedChild() == 1) {
            loginActivityViewFlipper.setInAnimation(this, R.anim.abc_slide_in_bottom);
            loginActivityViewFlipper.setOutAnimation(this, R.anim.abc_slide_out_top);
            loginActivityViewFlipper.showPrevious();
        } else {
            Log.e("SL_LOGIN_ACTIVITY", "Attempted to flip to login screen but are already on it!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class DelayForLogin implements Runnable {

        @Override
        public void run() {
            if (!AppController.isUserLoggedIn())
                delayForLoginCallback.postDelayed(new DelayForLogin(), 500);
            else
                thisActivity.finish();
        }
    }
}
