package com.company.rss.rss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.company.rss.rss.models.User;
import com.company.rss.rss.persistence.UserPrefs;
import com.company.rss.rss.restful_api.RESTMiddleware;
import com.company.rss.rss.restful_api.callbacks.UserCallback;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = getClass().getName();
    public static final String EXTRA_USER = "com.rss.rss.USER";
    private RESTMiddleware api;
    private User loggedUser;

    private Button loginButton;
    private TextView emailText;
    private TextView passwordText;
    private static final int REQUEST_SIGNUP = 0;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        //Verifies if this Activity was opened after a failed authentication
        Intent intent = getIntent();
        boolean authFailed = intent.getBooleanExtra("authFailed", false);
        if(authFailed==true) {
            Snackbar.make(findViewById(android.R.id.content), R.string.authentication_failed, Snackbar.LENGTH_LONG).show();
        }

        loginButton = findViewById(R.id.loginButton);
        TextView signUpTextView = findViewById(R.id.signUpTextView);
        emailText = findViewById(R.id.loginMailEditText);
        passwordText = findViewById(R.id.loginPasswordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Starting the Sign up activity...");

                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        //Instantiate the Middleware for the RESTful API's
        api = new RESTMiddleware(this);

        //Get a SharedPreferences instance
        UserPrefs prefs = new UserPrefs(context);

        //Get the User Logged in
        loggedUser = prefs.retrieveUser();
        //Skip Login Activity if User already persisted
        if(loggedUser != null) {
            onLoginSuccess();
        }
    }



    /**
     * Login procedure
     */
    public void login() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Logging in user...");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getText(R.string.authenticating));
        progressDialog.show();

        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        //Authentication and Persistence of the User
        Log.d(ArticleActivity.logTag + ":" + TAG, "Authenticating user...");
        api.getUserAuthentication(email, password, new UserCallback() {
            @Override
            public void onLoad(List<User> users) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser authentication " + users.size());


                for(User user: users){
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser authentication");
                    Log.d(ArticleActivity.logTag + ":" + TAG, "\nUser: " + user.getId() +  ", " + user.getName() + ", " + user.getSurname() + ", " + user.getEmail() + ", " + user.getPassword());

                    //Get logged user
                    if(users.isEmpty() == false) {
                        loggedUser = users.get(0);

                        //Get a SharedPreferences instance
                        UserPrefs prefs = new UserPrefs( context);

                        //Persist the User Logged in
                        prefs.storeUser(loggedUser);
                    }
                    progressDialog.dismiss();
                    onLoginSuccess();
                }
            }

            @Override
            public void onFailure() {
                Log.d(ArticleActivity.logTag + ":" + TAG, "\nFailure on: getUserAuthentication");
                progressDialog.dismiss();
                onLoginFailed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from Signup activity with RESULT_OK");
                Snackbar.make(findViewById(android.R.id.content), R.string.registration_completed_login, Snackbar.LENGTH_LONG).show();
            } else {
                Log.d(ArticleActivity.logTag + ":" + TAG, "Returned from Signup activity without RESULT_OK");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the LoadingActivity
        moveTaskToBack(true);
    }

    private void startLoadingActivity() {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra("logged-user", loggedUser);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void onLoginSuccess() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Login success");
        loginButton.setEnabled(true);

        startLoadingActivity();

        //Start a Loading Spinner Dialog
        /*final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();*/

    }

    public void onLoginFailed() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Login failed");

        Snackbar.make(findViewById(android.R.id.content), R.string.login_failed, Snackbar.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        Log.d(ArticleActivity.logTag + ":" + TAG, "Validating user...");

        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getText(R.string.email_not_valid));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getText(R.string.password_not_valid));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

}
